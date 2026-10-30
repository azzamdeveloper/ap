package com.example.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BrowserTab
import com.example.ui.theme.ZBorderGlow
import com.example.ui.theme.ZCyanElectric
import com.example.ui.theme.ZOrangeSunburst
import com.example.ui.theme.ZShieldGreen
import com.example.ui.theme.ZSpaceNavy
import com.example.ui.theme.ZSpaceNavyDark
import com.example.ui.theme.ZSurfaceCard
import com.example.ui.theme.ZSurfaceElevated
import com.example.ui.theme.ZSurfaceNavy
import com.example.ui.theme.ZTextMuted
import com.example.ui.theme.ZTextPrimary
import com.example.ui.theme.ZTextSecondary
import com.example.viewmodel.BrowserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabsSheet(
    viewModel: BrowserViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val tabs by viewModel.tabs.collectAsState()
    val activeTabId by viewModel.activeTabId.collectAsState()

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OPEN TABS (${tabs.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZCyanElectric,
                    letterSpacing = 1.sp
                )
                Row {
                    Button(
                        onClick = { viewModel.openNewTab() },
                        colors = ButtonDefaults.buttonColors(containerColor = ZCyanElectric),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("new_tab_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = ZSpaceNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Tab", color = ZSpaceNavy, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = ZTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tabs, key = { it.id }) { tab ->
                    TabCard(
                        tab = tab,
                        isActive = tab.id == activeTabId,
                        onSelect = { viewModel.selectTab(tab.id) },
                        onClose = { viewModel.closeTab(tab.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { viewModel.openNewTab(isIncognito = true) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = ZOrangeSunburst,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Incognito Tab", color = ZOrangeSunburst, fontSize = 11.sp)
                }

                if (tabs.size > 1) {
                    OutlinedButton(
                        onClick = {
                            val first = tabs.firstOrNull()?.id ?: return@OutlinedButton
                            tabs.forEach { if (it.id != first) viewModel.closeTab(it.id) }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close Others", color = ZTextSecondary, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TabCard(
    tab: BrowserTab,
    isActive: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = if (isActive) ZCyanElectric else ZBorderGlow,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onSelect() }
            .testTag("tab_item_${tab.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) ZSurfaceCard else ZSurfaceNavy
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (tab.isHome) Icons.Default.Home else Icons.Default.Language,
                    contentDescription = null,
                    tint = if (isActive) ZCyanElectric else ZTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close tab",
                        tint = ZTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Text(
                    text = if (tab.isHome) "Home" else tab.title.ifBlank { "Untitled" },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (tab.isHome) "zbrowser://home" else tab.url.ifBlank { "about:blank" },
                    fontSize = 10.sp,
                    color = ZTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
