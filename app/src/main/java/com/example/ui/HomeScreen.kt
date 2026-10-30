package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
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
fun HomeScreen(
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    val vpnStatus by VpnManager.vpnStatus.collectAsState()
    val selectedServer by VpnManager.selectedServer.collectAsState()
    val isAdBlockerActive by AdBlocker.isEnabled.collectAsState()
    val totalAdsBlocked by AdBlocker.totalAdsBlocked.collectAsState()
    val totalTrackers by AdBlocker.totalTrackersBlocked.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()

    // Breathing glow animation for the futuristic emblem
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Background from user's image 2 (futuristic cyber hexagon wallpaper)
        Image(
            painter = painterResource(id = R.drawable.z_bg_wallpaper),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Subtle gradient scrim overlay for visual contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ZSpaceNavy.copy(alpha = 0.65f),
                            ZSpaceNavyDark.copy(alpha = 0.88f),
                            ZSpaceNavyDark.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Brand Logo & Emblem Section
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .scale(pulseScale)
                        .padding(vertical = 8.dp)
                ) {
                    // Outer neon aura
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        ZCyanElectric.copy(alpha = 0.25f),
                                        ZOrangeRadiant.copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    // Official Z Logo cropped from user's image 1
                    Image(
                        painter = painterResource(id = R.drawable.z_logo_full),
                        contentDescription = "Z Browser Logo",
                        modifier = Modifier
                            .size(width = 220.dp, height = 130.dp)
                            .testTag("z_browser_home_logo"),
                        contentScale = ContentScale.Fit
                    )
                }

                Text(
                    text = "NEXT-GEN PRIVATE CYBER BROWSER",
                    color = ZCyanElectric,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                )
            }

            // Omnibox / Search & URL Input
            item {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = {
                        Text(
                            "Search or enter web URL...",
                            color = ZTextMuted,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = ZCyanElectric
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { searchText = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = ZTextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (searchText.isNotBlank()) {
                            viewModel.navigate(searchText)
                        }
                    }),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ZSurfaceElevated.copy(alpha = 0.9f),
                        unfocusedContainerColor = ZSurfaceElevated.copy(alpha = 0.8f),
                        focusedBorderColor = ZCyanElectric,
                        unfocusedBorderColor = ZBorderGlow,
                        focusedTextColor = ZTextPrimary,
                        unfocusedTextColor = ZTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                        .testTag("home_search_input")
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // VPN & AdBlocker Quick Controls Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // VPN Quick Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = 1.dp,
                                color = if (vpnStatus == VpnStatus.CONNECTED) ZShieldGreen else ZBorderGlow,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { viewModel.showVpnSheet.value = true }
                            .testTag("vpn_quick_card"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (vpnStatus == VpnStatus.CONNECTED) {
                                ZSurfaceCard.copy(alpha = 0.95f)
                            } else {
                                ZSurfaceNavy.copy(alpha = 0.85f)
                            }
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VpnKey,
                                        contentDescription = "VPN",
                                        tint = if (vpnStatus == VpnStatus.CONNECTED) ZShieldGreen else ZOrangeRadiant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Z-VPN",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = ZTextPrimary
                                    )
                                }
                                Switch(
                                    checked = vpnStatus == VpnStatus.CONNECTED,
                                    onCheckedChange = { VpnManager.toggleVpn(context) },
                                    modifier = Modifier.scale(0.75f),
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = ZShieldGreen,
                                        checkedTrackColor = ZShieldGreen.copy(alpha = 0.3f),
                                        uncheckedThumbColor = ZTextMuted,
                                        uncheckedTrackColor = ZSurfaceElevated
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (vpnStatus) {
                                    VpnStatus.CONNECTED -> "${selectedServer.flagEmoji} ${selectedServer.name}"
                                    VpnStatus.CONNECTING -> "Connecting..."
                                    VpnStatus.DISCONNECTED -> "Disconnected"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (vpnStatus == VpnStatus.CONNECTED) ZShieldGreenGlow else ZTextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (vpnStatus == VpnStatus.CONNECTED) "${selectedServer.pingMs}ms • Encrypted" else "Tap to choose node",
                                fontSize = 10.sp,
                                color = ZTextSecondary
                            )
                        }
                    }

                    // AdBlocker Quick Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = 1.dp,
                                color = if (isAdBlockerActive) ZCyanElectric else ZBorderGlow,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { viewModel.showAdBlockerSheet.value = true }
                            .testTag("adblocker_quick_card"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAdBlockerActive) {
                                ZSurfaceCard.copy(alpha = 0.95f)
                            } else {
                                ZSurfaceNavy.copy(alpha = 0.85f)
                            }
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "AdBlocker",
                                        tint = if (isAdBlockerActive) ZCyanElectric else ZTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AdShield",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = ZTextPrimary
                                    )
                                }
                                Switch(
                                    checked = isAdBlockerActive,
                                    onCheckedChange = { AdBlocker.setEnabled(it) },
                                    modifier = Modifier.scale(0.75f),
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = ZCyanElectric,
                                        checkedTrackColor = ZCyanElectric.copy(alpha = 0.3f),
                                        uncheckedThumbColor = ZTextMuted,
                                        uncheckedTrackColor = ZSurfaceElevated
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isAdBlockerActive) "$totalAdsBlocked Ads Blocked" else "Shield Paused",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isAdBlockerActive) ZCyanElectric else ZShieldRed,
                                maxLines = 1
                            )
                            Text(
                                text = "$totalTrackers trackers stopped",
                                fontSize = 10.sp,
                                color = ZTextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Quick Access / Bookmarks Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUICK ACCESS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZCyanElectric,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Manage",
                        fontSize = 11.sp,
                        color = ZOrangeSunburst,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { viewModel.showBookmarksSheet.value = true }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Grid of Bookmarks / Quick Links
            val chunkedBookmarks = bookmarks.chunked(4)
            items(chunkedBookmarks.size) { rowIndex ->
                val rowItems = chunkedBookmarks[rowIndex]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowItems.forEach { bookmark ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.navigate(bookmark.url) }
                                .padding(4.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, ZBorderGlow, RoundedCornerShape(14.dp)),
                                color = ZSurfaceElevated.copy(alpha = 0.9f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = bookmark.title.take(1).uppercase(),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        color = when (bookmark.category) {
                                            "Search" -> ZCyanElectric
                                            "Privacy" -> ZShieldGreen
                                            "Media" -> ZOrangeSunburst
                                            "News" -> ZGlobeBlue
                                            else -> ZCyanElectric
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = bookmark.title,
                                fontSize = 11.sp,
                                color = ZTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    // Fill remaining slots in row if less than 4
                    if (rowItems.size < 4) {
                        for (i in 0 until (4 - rowItems.size)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Security & Privacy Banner
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, ZBorderGlow.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ZSurfaceNavy.copy(alpha = 0.75f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = ZOrangeRadiant,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Zero-Trace Privacy Architecture",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZTextPrimary
                            )
                            Text(
                                text = "Encrypted DNS-over-HTTPS • Anti-fingerprinting • Automated tracker filter enabled.",
                                fontSize = 11.sp,
                                color = ZTextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
