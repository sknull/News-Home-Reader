package de.visualdigits.newshomereader.data.repository

expect class ConnectivityManager {
    fun isInternetAvailable(): Boolean
}
