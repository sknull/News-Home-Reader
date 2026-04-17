package de.visualdigits.newshomereader.domain.model.platform

enum class ConnectivityMode(
    val isFreeOfCharge: Boolean
) {

    wifi(true),
    cellular(false),
    ethernet(true),
    disconnected(true)
}
