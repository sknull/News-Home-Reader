package de.visualdigits.newshomereader.data.model

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

private const val ALIAS = "webdav_key"
private const val TRANSFORMATION = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"

class AndroidCryptoBox(private val basePath: String) : CryptoBox {

    override fun encrypt(value: String): String {
        if (value.isEmpty()) return ""

        val key = getOrCreateKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)

        // Cipher generiert hier automatisch einen neuen, zufälligen IV
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(value.encodeToByteArray())

        // Wir speichern "IV:Passwort" als Base64
        val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
        val encryptedBase64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)

        return "$ivBase64:$encryptedBase64"
    }

    override fun decrypt(value: String): String {
        if (value.isEmpty() || !value.contains(":")) return value

        try {
            val parts = value.split(":")
            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val encryptedBytes = Base64.decode(parts[1], Base64.NO_WRAP)

            val key = getOrCreateKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)

            // Hier füttern wir den IV aus der Datenbank wieder ein
            cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes)
        } catch (e: Exception) {
            // Falls Entschlüsselung fehlschlägt (z.B. falscher Key), geben wir zur Sicherheit nichts zurück
            return ""
        }
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

        if (!keyStore.containsAlias(ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyGenerator.init(
                KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setKeySize(256)
                    .build()
            )
            keyGenerator.generateKey()
        }
        return keyStore.getKey(ALIAS, null) as SecretKey
    }
}
