package com.example.adblocker

import android.content.Context
import android.net.Uri
import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object AdBlocker {
    private val blockedDomains = hashSetOf(
        "doubleclick.net",
        "googleadservices.com",
        "googlesyndication.com",
        "adservice.google.com",
        "pagead2.googlesyndication.com",
        "adnxs.com",
        "taboola.com",
        "outbrain.com",
        "scorecardresearch.com",
        "zedo.com",
        "popads.net",
        "popcash.net",
        "adroll.com",
        "moatads.com",
        "amazon-adsystem.com",
        "criteo.com",
        "criteo.net",
        "rubiconproject.com",
        "advertising.com",
        "adcolony.com",
        "applovin.com",
        "vungle.com",
        "admob.com",
        "bidswitch.net",
        "casalemedia.com",
        "openx.net",
        "pubmatic.com",
        "smartadserver.com",
        "yieldmo.com",
        "exponential.com",
        "serving-sys.com",
        "adform.net",
        "quantserve.com",
        "chartbeat.com",
        "hotjar.com",
        "clicktale.net",
        "crazyegg.com",
        "adtechus.com",
        "adblade.com",
        "revcontent.com",
        "mgid.com",
        "trafficjunky.com",
        "juicyads.com",
        "exoclick.com",
        "propellerads.com",
        "infolinks.com",
        "chitika.com",
        "zergnet.com",
        "sovrn.com",
        "lijit.com",
        "spotxchange.com",
        "flashtalking.com",
        "mathtag.com",
        "bluekai.com"
    )

    private val _isEnabled = MutableStateFlow(true)
    val isEnabled = _isEnabled.asStateFlow()

    private val _totalAdsBlocked = MutableStateFlow(142)
    val totalAdsBlocked = _totalAdsBlocked.asStateFlow()

    private val _totalTrackersBlocked = MutableStateFlow(89)
    val totalTrackersBlocked = _totalTrackersBlocked.asStateFlow()

    private val whitelistedHosts = hashSetOf<String>()

    fun setEnabled(enabled: Boolean) {
        _isEnabled.value = enabled
    }

    fun isAd(url: String?): Boolean {
        if (!_isEnabled.value || url.isNullOrBlank()) return false
        val uri = try {
            Uri.parse(url)
        } catch (_: Exception) {
            return false
        }
        val host = uri.host?.lowercase(Locale.ROOT) ?: return false

        if (whitelistedHosts.contains(host)) return false

        for (domain in blockedDomains) {
            if (host == domain || host.endsWith(".$domain")) {
                _totalAdsBlocked.value += 1
                if (domain.contains("scorecard") || domain.contains("quantserve") || domain.contains("hotjar") || domain.contains("chartbeat") || domain.contains("bluekai")) {
                    _totalTrackersBlocked.value += 1
                }
                return true
            }
        }
        return false
    }

    fun createEmptyResponse(): WebResourceResponse {
        return WebResourceResponse(
            "text/plain",
            "UTF-8",
            ByteArrayInputStream(ByteArray(0))
        )
    }

    fun toggleWhitelist(host: String): Boolean {
        return if (whitelistedHosts.contains(host)) {
            whitelistedHosts.remove(host)
            false
        } else {
            whitelistedHosts.add(host)
            true
        }
    }

    fun isWhitelisted(host: String): Boolean = whitelistedHosts.contains(host)

    // Cosmetic filter CSS script to hide blank spaces and banner holders
    val cosmeticInjectionJs: String = """
        (function() {
            var css = 'ins.adsbygoogle, .ad-placeholder, [id^="google_ads"], [class*="ad-banner"], [class*="advertisement"], [class*="sponsored-post"], iframe[src*="doubleclick"], iframe[src*="adnxs"], .taboola-placeholder { display: none !important; height: 0 !important; visibility: hidden !important; }';
            var head = document.head || document.getElementsByTagName('head')[0];
            if (head) {
                var style = document.createElement('style');
                style.type = 'text/css';
                style.appendChild(document.createTextNode(css));
                head.appendChild(style);
            }
        })();
    """.trimIndent()
}
