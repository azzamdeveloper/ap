package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.adblocker.AdBlocker
import com.example.ui.theme.*
import com.example.viewmodel.BrowserViewModel
import com.example.vpn.VpnManager
import com.example.vpn.VpnStatus

@Composable
fun ZBrowserScreen(
    viewModel: BrowserViewModel
) {
    val tabs by viewModel.tabs.collectAsState()
    val activeTabId by viewModel.activeTabId.collectAsState()
    val currentTab = viewModel.currentTab ?: tabs.first()
    val vpnStatus by VpnManager.vpnStatus.collectAsState()
    val isAdBlockerActive by AdBlocker.isEnabled.collectAsState()

    val showTabsSheet by viewModel.showTabsSheet.collectAsState()
    val showVpnSheet by viewModel.showVpnSheet.collectAsState()
    val showAdBlockerSheet by viewModel.showAdBlockerSheet.collectAsState()
    val showMenuSheet by viewModel.showMenuSheet.collectAsState()
    val showBookmarksSheet by viewModel.showBookmarksSheet.collectAsState()
    val showHistorySheet by viewModel.showHistorySheet.collectAsState()

    val focusManager = LocalFocusManager.current
    var urlBarText by remember { mutableStateOf("") }
    var isEditingUrl by remember { mutableStateOf(false) }

    LaunchedEffect(currentTab.url, currentTab.isHome) {
        if (!isEditingUrl) {
            urlBarText = if (currentTab.isHome) "" else currentTab.url
        }
    }

    // Hardware/Gesture back handler
    BackHandler(enabled = true) {
        when {
            showTabsSheet -> viewModel.showTabsSheet.value = false
            showVpnSheet -> viewModel.showVpnSheet.value = false
            showAdBlockerSheet -> viewModel.showAdBlockerSheet.value = false
            showMenuSheet -> viewModel.showMenuSheet.value = false
            showBookmarksSheet -> viewModel.showBookmarksSheet.value = false
            showHistorySheet -> viewModel.showHistorySheet.value = false
            !currentTab.isHome -> viewModel.goBack()
            tabs.size > 1 -> viewModel.closeTab(currentTab.id)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ZSpaceNavyDark,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZSpaceNavy)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // Top Bar with Z Logo, Omnibox, and Security Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Z Logo Emblem Icon (Tapping navigates to Home)
                    Image(
                        painter = painterResource(id = R.drawable.z_logo_emblem),
                        contentDescription = "Z Browser Home",
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.goHome() }
                            .testTag("top_z_logo")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Omnibox Bar
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(21.dp))
                            .border(
                                width = 1.dp,
                                color = if (isEditingUrl) ZCyanElectric else ZBorderGlow,
                                shape = RoundedCornerShape(21.dp)
                            ),
                        color = ZSurfaceElevated
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Lock icon / Home indicator
                            Icon(
                                imageVector = if (currentTab.isHome) Icons.Default.Home else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (currentTab.isHome) ZCyanElectric else ZShieldGreen,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Editable URL / Search input
                            BasicTextField(
                                value = if (isEditingUrl) urlBarText else if (currentTab.isHome) "Search or type URL" else (currentTab.url.ifBlank { currentTab.title }),
                                onValueChange = {
                                    urlBarText = it
                                    isEditingUrl = true
                                },
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = if (currentTab.isHome && !isEditingUrl) ZTextMuted else ZTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(ZCyanElectric),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                                keyboardActions = KeyboardActions(onGo = {
                                    focusManager.clearFocus()
                                    isEditingUrl = false
                                    if (urlBarText.isNotBlank()) {
                                        viewModel.navigate(urlBarText)
                                    }
                                }),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("omnibox_input")
                            )

                            // Action buttons inside URL bar
                            if (isEditingUrl && urlBarText.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        urlBarText = ""
                                    },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = ZTextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            } else if (!currentTab.isHome) {
                                // Reload button
                                IconButton(
                                    onClick = { viewModel.reload() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Reload",
                                        tint = ZCyanElectric,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // AdShield Badge
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isAdBlockerActive) ZCyanElectric.copy(alpha = 0.15f) else ZShieldRed.copy(alpha = 0.15f),
                                    modifier = Modifier
                                        .clickable { viewModel.showAdBlockerSheet.value = true }
                                        .padding(start = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = if (isAdBlockerActive) ZCyanElectric else ZShieldRed,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${currentTab.blockedAdsCount}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isAdBlockerActive) ZCyanElectric else ZShieldRed
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Menu button
                    IconButton(
                        onClick = { viewModel.showMenuSheet.value = true },
                        modifier = Modifier.testTag("top_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = ZTextPrimary
                        )
                    }
                }

                // Progress Bar with neon glowing gradient
                AnimatedVisibility(
                    visible = currentTab.isLoading && !currentTab.isHome,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    LinearProgressIndicator(
                        progress = { currentTab.progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.5.dp),
                        color = ZCyanElectric,
                        trackColor = Color.Transparent
                    )
                }
            }
        },
        bottomBar = {
            // Modern Cyber Navigation Toolbar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                color = ZSpaceNavy,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back
                    IconButton(
                        onClick = { viewModel.goBack() },
                        enabled = currentTab.canGoBack || !currentTab.isHome,
                        modifier = Modifier.testTag("bottom_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (currentTab.canGoBack || !currentTab.isHome) ZTextPrimary else ZTextMuted.copy(alpha = 0.4f)
                        )
                    }

                    // Forward
                    IconButton(
                        onClick = { viewModel.goForward() },
                        enabled = currentTab.canGoForward,
                        modifier = Modifier.testTag("bottom_forward_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Forward",
                            tint = if (currentTab.canGoForward) ZTextPrimary else ZTextMuted.copy(alpha = 0.4f)
                        )
                    }

                    // Home Button
                    IconButton(
                        onClick = { viewModel.goHome() },
                        modifier = Modifier.testTag("bottom_home_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = if (currentTab.isHome) ZCyanElectric else ZTextSecondary
                        )
                    }

                    // VPN Quick Tunnel Button (glowing indicator)
                    IconButton(
                        onClick = { viewModel.showVpnSheet.value = true },
                        modifier = Modifier.testTag("bottom_vpn_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (vpnStatus == VpnStatus.CONNECTED) {
                                    Badge(
                                        containerColor = ZShieldGreen,
                                        modifier = Modifier.size(8.dp)
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = "VPN Shield",
                                tint = if (vpnStatus == VpnStatus.CONNECTED) ZShieldGreen else ZOrangeSunburst
                            )
                        }
                    }

                    // Tab Switcher with tab count badge
                    IconButton(
                        onClick = { viewModel.showTabsSheet.value = true },
                        modifier = Modifier.testTag("bottom_tabs_button")
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, ZTextPrimary),
                            color = Color.Transparent,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${tabs.size}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ZTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (currentTab.isHome) {
                HomeScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                WebViewContainer(
                    tab = currentTab,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Bottom Sheets
    if (showTabsSheet) {
        TabsSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.showTabsSheet.value = false }
        )
    }

    if (showVpnSheet) {
        VpnBottomSheet(
            onDismiss = { viewModel.showVpnSheet.value = false }
        )
    }

    if (showAdBlockerSheet) {
        AdBlockerSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.showAdBlockerSheet.value = false }
        )
    }

    if (showMenuSheet) {
        MenuSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.showMenuSheet.value = false }
        )
    }

    if (showBookmarksSheet) {
        BookmarksSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.showBookmarksSheet.value = false }
        )
    }

    if (showHistorySheet) {
        HistorySheet(
            viewModel = viewModel,
            onDismiss = { viewModel.showHistorySheet.value = false }
        )
    }
}
