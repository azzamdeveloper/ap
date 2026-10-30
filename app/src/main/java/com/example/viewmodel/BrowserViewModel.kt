package com.example.viewmodel

import android.webkit.URLUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adblocker.AdBlocker
import com.example.data.Bookmark
import com.example.data.BrowserTab
import com.example.data.HistoryItem
import com.example.data.SearchEngine
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class WebViewCommand {
    data class LoadUrl(val url: String) : WebViewCommand()
    object GoBack : WebViewCommand()
    object GoForward : WebViewCommand()
    object Reload : WebViewCommand()
    object Stop : WebViewCommand()
}

class BrowserViewModel : ViewModel() {

    private val defaultBookmarks = listOf(
        Bookmark(title = "Google", url = "https://www.google.com", category = "Search"),
        Bookmark(title = "DuckDuckGo", url = "https://duckduckgo.com", category = "Privacy"),
        Bookmark(title = "Wikipedia", url = "https://www.wikipedia.org", category = "Reference"),
        Bookmark(title = "YouTube", url = "https://www.youtube.com", category = "Media"),
        Bookmark(title = "GitHub", url = "https://github.com", category = "Dev"),
        Bookmark(title = "Reddit", url = "https://www.reddit.com", category = "Community"),
        Bookmark(title = "Detik", url = "https://www.detik.com", category = "News"),
        Bookmark(title = "Kompas", url = "https://www.kompas.com", category = "News")
    )

    private val initialTab = BrowserTab(title = "Home", url = "", isHome = true)

    private val _tabs = MutableStateFlow<List<BrowserTab>>(listOf(initialTab))
    val tabs = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow(initialTab.id)
    val activeTabId = _activeTabId.asStateFlow()

    private val _searchEngine = MutableStateFlow(SearchEngine.GOOGLE)
    val searchEngine = _searchEngine.asStateFlow()

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(defaultBookmarks)
    val bookmarks = _bookmarks.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history = _history.asStateFlow()

    // Sheet visibility flags
    val showTabsSheet = MutableStateFlow(false)
    val showVpnSheet = MutableStateFlow(false)
    val showAdBlockerSheet = MutableStateFlow(false)
    val showMenuSheet = MutableStateFlow(false)
    val showBookmarksSheet = MutableStateFlow(false)
    val showHistorySheet = MutableStateFlow(false)

    // WebView commands
    private val _commandFlow = MutableSharedFlow<WebViewCommand>()
    val commandFlow = _commandFlow.asSharedFlow()

    val currentTab: BrowserTab?
        get() = _tabs.value.find { it.id == _activeTabId.value } ?: _tabs.value.firstOrNull()

    fun navigate(input: String) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return

        val formattedUrl = when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> _searchEngine.value.searchUrl + java.net.URLEncoder.encode(trimmed, "UTF-8")
        }

        updateActiveTab { it.copy(url = formattedUrl, isHome = false, title = trimmed, isLoading = true) }
        viewModelScope.launch {
            _commandFlow.emit(WebViewCommand.LoadUrl(formattedUrl))
        }
    }

    fun openNewTab(url: String = "", isIncognito: Boolean = false) {
        val newTab = BrowserTab(
            title = if (url.isEmpty()) "Home" else "Loading...",
            url = url,
            isHome = url.isEmpty(),
            isIncognito = isIncognito
        )
        _tabs.update { it + newTab }
        _activeTabId.value = newTab.id
        showTabsSheet.value = false

        if (url.isNotEmpty()) {
            viewModelScope.launch {
                _commandFlow.emit(WebViewCommand.LoadUrl(url))
            }
        }
    }

    fun closeTab(tabId: String) {
        val currentList = _tabs.value
        if (currentList.size <= 1) {
            // Reset to clean home tab if last tab is closed
            val resetTab = BrowserTab(title = "Home", url = "", isHome = true)
            _tabs.value = listOf(resetTab)
            _activeTabId.value = resetTab.id
            return
        }

        val nextList = currentList.filterNot { it.id == tabId }
        _tabs.value = nextList

        if (_activeTabId.value == tabId) {
            _activeTabId.value = nextList.last().id
        }
    }

    fun selectTab(tabId: String) {
        _activeTabId.value = tabId
        showTabsSheet.value = false
    }

    fun goHome() {
        updateActiveTab { it.copy(isHome = true, url = "", title = "Home", isLoading = false, progress = 0) }
    }

    fun goBack() {
        val tab = currentTab ?: return
        if (tab.canGoBack) {
            viewModelScope.launch {
                _commandFlow.emit(WebViewCommand.GoBack)
            }
        } else {
            goHome()
        }
    }

    fun goForward() {
        viewModelScope.launch {
            _commandFlow.emit(WebViewCommand.GoForward)
        }
    }

    fun reload() {
        val tab = currentTab ?: return
        if (!tab.isHome && tab.url.isNotEmpty()) {
            viewModelScope.launch {
                _commandFlow.emit(WebViewCommand.Reload)
            }
        }
    }

    fun toggleDesktopMode() {
        val tab = currentTab ?: return
        val newMode = !tab.isDesktopMode
        updateActiveTab { it.copy(isDesktopMode = newMode) }
        reload()
    }

    fun updateTabUrl(tabId: String, url: String) {
        _tabs.update { list ->
            list.map { if (it.id == tabId) it.copy(url = url, isHome = false) else it }
        }
    }

    fun updateTabTitle(tabId: String, title: String) {
        _tabs.update { list ->
            list.map { if (it.id == tabId) it.copy(title = title) else it }
        }
    }

    fun updateTabProgress(tabId: String, progress: Int, isLoading: Boolean) {
        _tabs.update { list ->
            list.map { if (it.id == tabId) it.copy(progress = progress, isLoading = isLoading) else it }
        }
    }

    fun updateTabNavigation(tabId: String, canBack: Boolean, canForward: Boolean) {
        _tabs.update { list ->
            list.map { if (it.id == tabId) it.copy(canGoBack = canBack, canGoForward = canForward) else it }
        }
    }

    fun incrementBlockedAds(tabId: String) {
        _tabs.update { list ->
            list.map { if (it.id == tabId) it.copy(blockedAdsCount = it.blockedAdsCount + 1) else it }
        }
    }

    fun addBookmark(title: String, url: String) {
        if (url.isBlank()) return
        val newBookmark = Bookmark(title = title.ifBlank { url }, url = url)
        _bookmarks.update { it + newBookmark }
    }

    fun removeBookmark(id: String) {
        _bookmarks.update { list -> list.filterNot { it.id == id } }
    }

    fun recordHistory(title: String, url: String) {
        val tab = currentTab
        if (tab?.isIncognito == true || url.isBlank() || url.startsWith("about:") || url.startsWith("data:")) return
        val item = HistoryItem(title = title.ifBlank { url }, url = url)
        _history.update { listOf(item) + it.take(199) }
    }

    fun clearHistory() {
        _history.value = emptyList()
    }

    fun setSearchEngine(engine: SearchEngine) {
        _searchEngine.value = engine
    }

    private fun updateActiveTab(block: (BrowserTab) -> BrowserTab) {
        val id = _activeTabId.value
        _tabs.update { list ->
            list.map { if (it.id == id) block(it) else it }
        }
    }
}
