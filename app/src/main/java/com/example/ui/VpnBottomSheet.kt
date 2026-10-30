package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VpnServer
import com.example.ui.theme.ZBorderGlow
import com.example.ui.theme.ZCyanDeep
import com.example.ui.theme.ZCyanElectric
import com.example.ui.theme.ZOrangeRadiant
import com.example.ui.theme.ZOrangeSunburst
import com.example.ui.theme.ZShieldGreen
import com.example.ui.theme.ZShieldGreenGlow
import com.example.ui.theme.ZShieldRed
import com.example.ui.theme.ZSpaceNavy
import com.example.ui.theme.ZSpaceNavyDark
import com.example.ui.theme.ZSurfaceCard
import com.example.ui.theme.ZSurfaceElevated
import com.example.ui.theme.ZSurfaceNavy
import com.example.ui.theme.ZTextMuted
import com.example.ui.theme.ZTextPrimary
import com.example.ui.theme.ZTextSecondary
import com.example.vpn.VpnManager
import com.example.vpn.VpnStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VpnBottomSheet(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val vpnStatus by VpnManager.vpnStatus.collectAsState()
    val selectedServer by VpnManager.selectedServer.collectAsState()
    val downloadSpeed by VpnManager.downloadSpeedKbps.collectAsState()
    val uploadSpeed by VpnManager.uploadSpeedKbps.collectAsState()
    val connectedDuration by VpnManager.connectedDurationSec.collectAsState()
    val totalDataMb by VpnManager.totalDataTransferredMb.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "powerPulse")
    val powerGlowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "powerGlowScale"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ZSpaceNavyDark,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = ZCyanElectric,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Z-VPN TUNNEL SHIELD",
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

            // Main Power Button with Animated Aura
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(130.dp)
                    .clickable { VpnManager.toggleVpn(context) }
                    .testTag("vpn_power_button")
            ) {
                // Pulsing outer halo when connected
                if (vpnStatus == VpnStatus.CONNECTED) {
                    Box(
                        modifier = Modifier
                            .size(126.dp)
                            .scale(powerGlowScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        ZShieldGreen.copy(alpha = 0.35f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                // Inner Circular button
                Surface(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(
                                colors = when (vpnStatus) {
                                    VpnStatus.CONNECTED -> listOf(ZShieldGreen, ZCyanElectric)
                                    VpnStatus.CONNECTING -> listOf(ZOrangeRadiant, ZOrangeSunburst)
                                    VpnStatus.DISCONNECTED -> listOf(ZBorderGlow, ZSurfaceCard)
                                }
                            ),
                            shape = CircleShape
                        ),
                    color = ZSurfaceNavy
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Toggle VPN",
                            tint = when (vpnStatus) {
                                VpnStatus.CONNECTED -> ZShieldGreen
                                VpnStatus.CONNECTING -> ZOrangeSunburst
                                VpnStatus.DISCONNECTED -> ZTextMuted
                            },
                            modifier = Modifier.size(46.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Connection Status Text
            Text(
                text = when (vpnStatus) {
                    VpnStatus.CONNECTED -> "SECURE & CONNECTED"
                    VpnStatus.CONNECTING -> "ESTABLISHING ENCRYPTED TUNNEL..."
                    VpnStatus.DISCONNECTED -> "NOT PROTECTED (TAP TO CONNECT)"
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = when (vpnStatus) {
                    VpnStatus.CONNECTED -> ZShieldGreenGlow
                    VpnStatus.CONNECTING -> ZOrangeRadiant
                    VpnStatus.DISCONNECTED -> ZTextMuted
                },
                letterSpacing = 0.5.sp
            )

            if (vpnStatus == VpnStatus.CONNECTED) {
                Text(
                    text = "Time Connected: ${VpnManager.formatDuration(connectedDuration)} • IP: ${selectedServer.ipAddress}",
                    fontSize = 11.sp,
                    color = ZCyanElectric,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Live Speed & Telemetry HUD Card
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, ZBorderGlow, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = ZSurfaceElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("DOWNLOAD", fontSize = 10.sp, color = ZTextSecondary, fontWeight = FontWeight.Bold)
                            Text("${downloadSpeed / 1024f}".take(4) + " MB/s", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ZCyanElectric)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("UPLOAD", fontSize = 10.sp, color = ZTextSecondary, fontWeight = FontWeight.Bold)
                            Text("${uploadSpeed / 1024f}".take(4) + " MB/s", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ZOrangeSunburst)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("DATA SAVED", fontSize = 10.sp, color = ZTextSecondary, fontWeight = FontWeight.Bold)
                            Text("${String.format("%.1f", totalDataMb)} MB", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ZShieldGreen)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Server Location Selection List
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SELECT PROXY NODE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZCyanElectric,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Protocol: WireGuard/TLS 1.3",
                    fontSize = 10.sp,
                    color = ZTextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(VpnManager.serverList) { server ->
                    ServerListItem(
                        server = server,
                        isSelected = server.id == selectedServer.id,
                        onClick = { VpnManager.selectServer(server) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ServerListItem(
    server: VpnServer,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) ZCyanElectric else ZBorderGlow.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .testTag("server_item_${server.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ZSurfaceCard else ZSurfaceNavy.copy(alpha = 0.9f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = server.flagEmoji,
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = server.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZTextPrimary
                        )
                        if (server.isRecommended) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ZOrangeRadiant.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "FASTEST",
                                    color = ZOrangeSunburst,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "${server.city} • IP ${server.ipAddress}",
                        fontSize = 10.sp,
                        color = ZTextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${server.pingMs} ms",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = when {
                        server.pingMs < 30 -> ZShieldGreen
                        server.pingMs < 90 -> ZCyanElectric
                        else -> ZOrangeSunburst
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = ZCyanElectric,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
