package de.visualdigits.newshomereader.data.model

interface CryptoBox {
    fun encrypt(value: String): String
    fun decrypt(value: String): String
}
