package com.example.ui

import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adblocker.AdBlocker
import com.example.ui.theme.ZBorderGlow
import com.example.ui.theme.ZCyanElectric
import com.example.ui.theme.ZOrangeSunburst
import com.example.ui.theme.ZShieldGreen
import com.example.ui.theme.ZShieldRed
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
fun AdBlockerSheet(
    viewModel: BrowserViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isEnabled by AdBlocker.isEnabled.collectAsState()
    val totalAds by AdBlocker.totalAdsBlocked.collectAsState()
    val totalTrackers by AdBlocker.totalTrackersBlocked.collectAsState()
    val currentTab = viewModel.currentTab

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = ZCyanElectric,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Z ADSHIELD PROTECTION",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZTextPrimary,
                        letterSpacing = 1.sp
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = ZTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Master Toggle Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        if (isEnabled) ZCyanElectric else ZBorderGlow,
                        RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = ZSurfaceElevated)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isEnabled) "AdBlocker is ACTIVE" else "AdBlocker is PAUSED",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isEnabled) ZCyanElectric else ZShieldRed
                        )
                        Text(
                            text = if (isEnabled) "Filtering banners, popups & tracking telemetry" else "Web pages will display original ad networks",
                            fontSize = 11.sp,
                            color = ZTextSecondary
                        )
                    }
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { AdBlocker.setEnabled(it) },
                        modifier = Modifier.testTag("adblocker_master_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ZCyanElectric,
                            checkedTrackColor = ZCyanElectric.copy(alpha = 0.35f),
                            uncheckedThumbColor = ZTextMuted,
                            uncheckedTrackColor = ZSurfaceNavy
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Matrix
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "ADS BLOCKED",
                    value = "$totalAds",
                    icon = Icons.Default.Block,
                    iconTint = ZCyanElectric,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "TRACKERS STOPPED",
                    value = "$totalTrackers",
                    icon = Icons.Default.VisibilityOff,
                    iconTint = ZShieldGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val dataSavedMb = (totalAds * 0.18f)
                StatCard(
                    title = "EST. DATA SAVED",
                    value = "${String.format("%.1f", dataSavedMb)} MB",
                    icon = Icons.Default.Save,
                    iconTint = ZOrangeSunburst,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "PAGE SPEED BOOST",
                    value = "+48%",
                    icon = Icons.Default.FlashOn,
                    iconTint = ZCyanElectric,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Site Status
            if (currentTab != null && !currentTab.isHome && currentTab.url.isNotBlank()) {
                val uri = try { android.net.Uri.parse(currentTab.url) } catch (_: Exception) { null }
                val host = uri?.host ?: ""
                if (host.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, ZBorderGlow, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = ZSurfaceNavy)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Current Site: $host",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZTextPrimary
                                )
                                Text(
                                    text = "${currentTab.blockedAdsCount} ads blocked on this page",
                                    fontSize = 10.sp,
                                    color = ZCyanElectric
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, ZBorderGlow.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = ZSurfaceCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ZTextPrimary
            )
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = ZTextSecondary,
                letterSpacing = 0.5.sp
            )
        }
    }
}
