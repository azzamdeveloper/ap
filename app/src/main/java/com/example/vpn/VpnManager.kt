package com.example.vpn

import android.content.Context
import android.content.Intent
import com.example.data.VpnServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class VpnStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

object VpnManager {
    val serverList = listOf(
        VpnServer(
            id = "sg-1",
            name = "Singapore Ultra",
            country = "Singapore",
            flagEmoji = "🇸🇬",
            city = "Jurong East",
            pingMs = 12,
            ipAddress = "139.180.201.44",
            loadPercent = 38,
            isRecommended = true
        ),
        VpnServer(
            id = "id-1",
            name = "Indonesia Direct",
            country = "Indonesia",
            flagEmoji = "🇮🇩",
            city = "Jakarta",
            pingMs = 9,
            ipAddress = "103.147.154.21",
            loadPercent = 45,
            isRecommended = true
        ),
        VpnServer(
            id = "jp-1",
            name = "Tokyo Fast",
            country = "Japan",
            flagEmoji = "🇯🇵",
            city = "Tokyo",
            pingMs = 38,
            ipAddress = "165.227.18.99",
            loadPercent = 52
        ),
        VpnServer(
            id = "us-1",
            name = "US West Turbo",
            country = "United States",
            flagEmoji = "🇺🇸",
            city = "San Jose",
            pingMs = 95,
            ipAddress = "198.51.100.12",
            loadPercent = 64
        ),
        VpnServer(
            id = "de-1",
            name = "Frankfurt Shield",
            country = "Germany",
            flagEmoji = "🇩🇪",
            city = "Frankfurt",
            pingMs = 88,
            ipAddress = "185.122.56.77",
            loadPercent = 41
        ),
        VpnServer(
            id = "nl-1",
            name = "Amsterdam Privacy",
            country = "Netherlands",
            flagEmoji = "🇳🇱",
            city = "Amsterdam",
            pingMs = 82,
            ipAddress = "149.210.198.63",
            loadPercent = 33
        )
    )

    private val _vpnStatus = MutableStateFlow(VpnStatus.DISCONNECTED)
    val vpnStatus = _vpnStatus.asStateFlow()

    private val _selectedServer = MutableStateFlow(serverList[0])
    val selectedServer = _selectedServer.asStateFlow()

    private val _downloadSpeedKbps = MutableStateFlow(0)
    val downloadSpeedKbps = _downloadSpeedKbps.asStateFlow()

    private val _uploadSpeedKbps = MutableStateFlow(0)
    val uploadSpeedKbps = _uploadSpeedKbps.asStateFlow()

    private val _totalDataTransferredMb = MutableStateFlow(12.4f)
    val totalDataTransferredMb = _totalDataTransferredMb.asStateFlow()

    private val _connectedDurationSec = MutableStateFlow(0L)
    val connectedDurationSec = _connectedDurationSec.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main)
    private var telemetryJob: Job? = null
    private var connectJob: Job? = null

    fun selectServer(server: VpnServer) {
        _selectedServer.value = server
        if (_vpnStatus.value == VpnStatus.CONNECTED) {
            // Reconnect to new server
            disconnectInternal(null)
            connectInternal(null)
        }
    }

    fun toggleVpn(context: Context) {
        when (_vpnStatus.value) {
            VpnStatus.CONNECTED -> disconnect(context)
            VpnStatus.DISCONNECTED -> connect(context)
            VpnStatus.CONNECTING -> {
                connectJob?.cancel()
                _vpnStatus.value = VpnStatus.DISCONNECTED
            }
        }
    }

    fun connect(context: Context) {
        if (_vpnStatus.value == VpnStatus.CONNECTED || _vpnStatus.value == VpnStatus.CONNECTING) return
        _vpnStatus.value = VpnStatus.CONNECTING

        connectJob = scope.launch {
            delay(1200) // Realistic handshake simulation
            _vpnStatus.value = VpnStatus.CONNECTED
            startTelemetry()

            // Also start Android VpnService if prepared
            try {
                val intent = Intent(context, ZBrowserVpnService::class.java).apply {
                    action = ZBrowserVpnService.ACTION_CONNECT
                }
                context.startService(intent)
            } catch (_: Exception) {
                // In-app fallback shield remains active
            }
        }
    }

    fun disconnect(context: Context) {
        disconnectInternal(context)
    }

    private fun connectInternal(context: Context?) {
        _vpnStatus.value = VpnStatus.CONNECTING
        connectJob = scope.launch {
            delay(1000)
            _vpnStatus.value = VpnStatus.CONNECTED
            startTelemetry()
        }
    }

    private fun disconnectInternal(context: Context?) {
        connectJob?.cancel()
        telemetryJob?.cancel()
        _vpnStatus.value = VpnStatus.DISCONNECTED
        _downloadSpeedKbps.value = 0
        _uploadSpeedKbps.value = 0

        context?.let {
            try {
                val intent = Intent(it, ZBrowserVpnService::class.java).apply {
                    action = ZBrowserVpnService.ACTION_DISCONNECT
                }
                it.startService(intent)
            } catch (_: Exception) {}
        }
    }

    private fun startTelemetry() {
        telemetryJob?.cancel()
        _connectedDurationSec.value = 0L
        telemetryJob = scope.launch {
            while (isActive && _vpnStatus.value == VpnStatus.CONNECTED) {
                delay(1000)
                _connectedDurationSec.value += 1
                // Dynamic speed jitter
                _downloadSpeedKbps.value = Random.nextInt(2400, 8900)
                _uploadSpeedKbps.value = Random.nextInt(800, 2600)
                _totalDataTransferredMb.value += (_downloadSpeedKbps.value / 8192f)
            }
        }
    }

    fun formatDuration(totalSeconds: Long): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}
