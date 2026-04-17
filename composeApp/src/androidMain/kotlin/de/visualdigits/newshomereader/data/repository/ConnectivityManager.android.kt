package de.visualdigits.newshomereader.data.repository

import android.Manifest
import android.content.Context
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import de.visualdigits.newshomereader.domain.model.platform.ConnectivityMode

actual class ConnectivityManager(
    private val context: Context
) {
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    actual fun connectivityMode(): ConnectivityMode {
        val connectivityManager = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        return connectivityManager.activeNetwork?.let { network ->
            connectivityManager.getNetworkCapabilities(network)?.let { capabilities ->
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectivityMode.wifi
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectivityMode.cellular
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectivityMode.ethernet
                    else -> ConnectivityMode.disconnected
                }
            } ?: ConnectivityMode.disconnected
        } ?: ConnectivityMode.disconnected
    }
}
