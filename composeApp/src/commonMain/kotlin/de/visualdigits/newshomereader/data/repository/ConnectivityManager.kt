package de.visualdigits.newshomereader.data.repository

import de.visualdigits.newshomereader.domain.model.platform.ConnectivityMode

expect class ConnectivityManager {
    fun connectivityMode(): ConnectivityMode
}
