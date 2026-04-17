package de.visualdigits.newshomereader.data.repository

import de.visualdigits.newshomereader.domain.model.platform.ConnectivityMode
import java.net.NetworkInterface

actual class ConnectivityManager {
    actual fun connectivityMode(): ConnectivityMode {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces().asSequence()
            val activeInterface = interfaces.firstOrNull {
                it.isUp && !it.isLoopback && it.hardwareAddress != null
            }
            when {
                activeInterface == null -> ConnectivityMode.disconnected
                isWifi(activeInterface) -> ConnectivityMode.wifi
                else -> ConnectivityMode.ethernet
            }
        } catch (e: Exception) {
            ConnectivityMode.disconnected
        }
    }

    private fun isWifi(ni: NetworkInterface): Boolean {
        val name = ni.name.lowercase()
        val displayName = ni.displayName.lowercase()

        return name.contains("wlan") ||
                name.contains("wifi") ||
                displayName.contains("wireless") ||
                displayName.contains("wi-fi")
    }
}
