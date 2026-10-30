package com.example.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.adblocker.AdBlocker
import com.example.data.BrowserTab
import com.example.viewmodel.BrowserViewModel
import com.example.viewmodel.WebViewCommand
import kotlinx.coroutines.flow.collectLatest

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewContainer(
    tab: BrowserTab,
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val webView = remember(tab.id) {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                builtInZoomControls = true
                displayZoomControls = false
                allowFileAccess = false
                allowContentAccess = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    safeBrowsingEnabled = true
                }
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                cacheMode = WebSettings.LOAD_DEFAULT
            }
        }
    }

    // Configure user agent for desktop mode
    LaunchedEffect(tab.isDesktopMode) {
        if (tab.isDesktopMode) {
            webView.settings.userAgentString = DESKTOP_USER_AGENT
        } else {
            webView.settings.userAgentString = null // Reset to default mobile user-agent
        }
    }

    // Bind Clients
    DisposableEffect(tab.id) {
        val defaultUserAgent = webView.settings.userAgentString

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val url = request?.url?.toString()
                if (AdBlocker.isAd(url)) {
                    view?.post {
                        viewModel.incrementBlockedAds(tab.id)
                    }
                    return AdBlocker.createEmptyResponse()
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                url?.let {
                    viewModel.updateTabUrl(tab.id, it)
                    viewModel.updateTabProgress(tab.id, 10, true)
                }
                view?.let {
                    viewModel.updateTabNavigation(tab.id, it.canGoBack(), it.canGoForward())
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                url?.let {
                    viewModel.updateTabUrl(tab.id, it)
                    viewModel.updateTabProgress(tab.id, 100, false)
                    viewModel.recordHistory(view?.title ?: it, it)
                }
                view?.let {
                    viewModel.updateTabNavigation(tab.id, it.canGoBack(), it.canGoForward())
                    // Inject cosmetic ad-hiding CSS
                    if (AdBlocker.isEnabled.value) {
                        it.evaluateJavascript(AdBlocker.cosmeticInjectionJs, null)
                    }
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                viewModel.updateTabProgress(tab.id, newProgress, newProgress < 100)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                title?.let { viewModel.updateTabTitle(tab.id, it) }
            }
        }

        onDispose {
            webView.stopLoading()
        }
    }

    // Handle initial navigation if tab already has a url
    LaunchedEffect(tab.id) {
        if (!tab.isHome && tab.url.isNotEmpty() && webView.url.isNullOrEmpty()) {
            webView.loadUrl(tab.url)
        }
    }

    // Observe WebView commands
    LaunchedEffect(tab.id) {
        viewModel.commandFlow.collectLatest { command ->
            when (command) {
                is WebViewCommand.LoadUrl -> {
                    webView.loadUrl(command.url)
                }
                is WebViewCommand.GoBack -> {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        viewModel.goHome()
                    }
                }
                is WebViewCommand.GoForward -> {
                    if (webView.canGoForward()) {
                        webView.goForward()
                    }
                }
                is WebViewCommand.Reload -> {
                    webView.reload()
                }
                is WebViewCommand.Stop -> {
                    webView.stopLoading()
                }
            }
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier.fillMaxSize()
    )
}
