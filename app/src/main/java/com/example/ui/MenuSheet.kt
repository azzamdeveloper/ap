package com.example.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SearchEngine
import com.example.ui.theme.ZBorderGlow
import com.example.ui.theme.ZCyanElectric
import com.example.ui.theme.ZOrangeSunburst
import com.example.ui.theme.ZShieldGreen
import com.example.ui.theme.ZSpaceNavyDark
import com.example.ui.theme.ZSurfaceCard
import com.example.ui.theme.ZSurfaceElevated
import com.example.ui.theme.ZTextMuted
import com.example.ui.theme.ZTextPrimary
import com.example.ui.theme.ZTextSecondary
import com.example.viewmodel.BrowserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuSheet(
    viewModel: BrowserViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentTab = viewModel.currentTab
    val searchEngine by viewModel.searchEngine.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ZSpaceNavyDark,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Z BROWSER CONTROLS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ZCyanElectric,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Bookmarks & History Row
            MenuItem(
                icon = Icons.Default.Bookmark,
                title = "Bookmarks",
                subtitle = "Saved websites & quick links",
                tint = ZCyanElectric,
                onClick = {
                    onDismiss()
                    viewModel.showBookmarksSheet.value = true
                }
            )

            MenuItem(
                icon = Icons.Default.History,
                title = "Browsing History",
                subtitle = "Recent web pages visited",
                tint = ZOrangeSunburst,
                onClick = {
                    onDismiss()
                    viewModel.showHistorySheet.value = true
                }
            )

            // VPN & AdBlocker direct openers
            MenuItem(
                icon = Icons.Default.VpnKey,
                title = "Z-VPN Tunnel Center",
                subtitle = "Server nodes & encrypted tunnel",
                tint = ZShieldGreen,
                onClick = {
                    onDismiss()
                    viewModel.showVpnSheet.value = true
                }
            )

            MenuItem(
                icon = Icons.Default.Shield,
                title = "AdShield & Anti-Tracking",
                subtitle = "Block telemetry, ads, and popups",
                tint = ZCyanElectric,
                onClick = {
                    onDismiss()
                    viewModel.showAdBlockerSheet.value = true
                }
            )

            // Desktop Mode Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = null,
                        tint = ZTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Desktop Site", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ZTextPrimary)
                        Text("Request desktop computer layout", fontSize = 11.sp, color = ZTextSecondary)
                    }
                }
                Switch(
                    checked = currentTab?.isDesktopMode == true,
                    onCheckedChange = { viewModel.toggleDesktopMode() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ZCyanElectric,
                        checkedTrackColor = ZCyanElectric.copy(alpha = 0.3f),
                        uncheckedThumbColor = ZTextMuted,
                        uncheckedTrackColor = ZSurfaceElevated
                    )
                )
            }

            // Share Page
            if (currentTab != null && !currentTab.isHome && currentTab.url.isNotBlank()) {
                MenuItem(
                    icon = Icons.Default.Share,
                    title = "Share Page",
                    subtitle = currentTab.url,
                    tint = ZTextSecondary,
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, currentTab.url)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share with"))
                        onDismiss()
                    }
                )

                MenuItem(
                    icon = Icons.Default.Bookmark,
                    title = "Add to Bookmarks",
                    subtitle = "Save current page for quick access",
                    tint = ZCyanElectric,
                    onClick = {
                        viewModel.addBookmark(currentTab.title, currentTab.url)
                        onDismiss()
                    }
                )
            }

            // Search Engine Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = ZTextSecondary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Default Search Engine", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ZTextPrimary)
                        Text(searchEngine.displayName, fontSize = 11.sp, color = ZCyanElectric)
                    }
                }
                Row {
                    SearchEngine.values().forEach { engine ->
                        Text(
                            text = engine.displayName.take(1),
                            color = if (engine == searchEngine) ZCyanElectric else ZTextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable { viewModel.setSearchEngine(engine) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun MenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ZTextPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = ZTextSecondary, maxLines = 1)
        }
    }
}
