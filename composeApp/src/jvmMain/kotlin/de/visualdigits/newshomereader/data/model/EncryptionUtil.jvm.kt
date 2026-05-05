package de.visualdigits.newshomereader.data.model

import java.io.File
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class JvmCryptoBox(private val basePath: String) : CryptoBox {

    private val keyFile = File(basePath, "vault.key")

    override fun encrypt(value: String): String {
        if (value.isEmpty()) return ""

        val key = getOrCreateKey()
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        // Generiert automatisch einen neuen IV
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(value.toByteArray(Charsets.UTF_8))

        // JVM Standard Base64 nutzen
        val ivBase64 = Base64.getEncoder().encodeToString(iv)
        val encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes)

        return "$ivBase64:$encryptedBase64"
    }

    override fun decrypt(value: String): String {
        if (value.isEmpty() || !value.contains(":")) return value

        return try {
            val parts = value.split(":")
            val iv = Base64.getDecoder().decode(parts[0])
            val encryptedBytes = Base64.getDecoder().decode(parts[1])

            val key = getOrCreateKey()
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

            // IV wieder einspeisen
            cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            ""
        }
    }

    private fun getOrCreateKey(): SecretKey {
        return if (keyFile.exists()) {
            val keyBytes = keyFile.readBytes()
            SecretKeySpec(keyBytes, "AES")
        } else {
            // Verzeichnis erstellen, falls nicht vorhanden
            keyFile.parentFile.mkdirs()

            // Neuen 256-Bit Key generieren
            val keyGen = KeyGenerator.getInstance("AES")
            keyGen.init(256)
            val key = keyGen.generateKey()

            // Key lokal wegspeichern
            keyFile.writeBytes(key.encoded)
            key
        }
    }
}
