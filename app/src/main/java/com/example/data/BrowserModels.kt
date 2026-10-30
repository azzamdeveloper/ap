package com.example.data

import java.util.UUID

data class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "New Tab",
    val url: String = "",
    val isHome: Boolean = true,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val blockedAdsCount: Int = 0,
    val isDesktopMode: Boolean = false,
    val isIncognito: Boolean = false
)

data class VpnServer(
    val id: String,
    val name: String,
    val country: String,
    val flagEmoji: String,
    val city: String,
    val pingMs: Int,
    val ipAddress: String,
    val loadPercent: Int,
    val isRecommended: Boolean = false
)

data class Bookmark(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val iconUrl: String = "",
    val category: String = "General"
)

data class HistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class SearchEngine(val displayName: String, val searchUrl: String) {
    GOOGLE("Google", "https://www.google.com/search?q="),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q="),
    BRAVE("Brave Search", "https://search.brave.com/search?q="),
    BING("Bing", "https://www.bing.com/search?q=")
}
