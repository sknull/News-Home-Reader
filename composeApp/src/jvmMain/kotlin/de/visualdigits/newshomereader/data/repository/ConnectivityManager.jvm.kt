package de.visualdigits.newshomereader.data.repository

import java.net.InetAddress

actual class ConnectivityManager {
    actual fun isInternetAvailable(): Boolean {
        return try {
            val address = InetAddress.getByName("www.google.com")
            address.hostAddress.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}
