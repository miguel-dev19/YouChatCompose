package cu.alexgi.youchat.data.remote

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor() {
    private val decod = "\u0084Éß\u0084Éß\u0084Éß\u0084Éß"

    fun encrypt(text: String, key: String = decod): String {
        if (key.length != 16) return encrypt(text)
        return try {
            val aesKey: Key = SecretKeySpec(key.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, aesKey)
            cipher.doFinal(text.toByteArray()).joinToString("") { String.format("%02X", it) }
        } catch (e: Exception) { encrypt(text) }
    }

    fun decrypt(text: String, key: String = decod): String {
        if (key.length != 16) return decrypt(text)
        return try {
            val aesKey: Key = SecretKeySpec(key.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, aesKey)
            val len = text.length
            val data = ByteArray(len / 2)
            for (i in 0 until len step 2) data[i / 2] = ((Character.digit(text[i], 16) shl 4) + Character.digit(text[i + 1], 16)).toByte()
            String(cipher.doFinal(data))
        } catch (e: Exception) { decrypt(text) }
    }

    fun encrypt(text: String): String = text.map { (it.code xor 0xAA).toChar() }.joinToString("")
    fun decrypt(text: String): String = text.map { (it.code xor 0xAA).toChar() }.joinToString("")

    fun md5(input: String): String = try {
        val md = java.security.MessageDigest.getInstance("MD5")
        md.digest(input.toByteArray(Charsets.UTF_8)).joinToString("") { Integer.toHexString((it.toInt() and 0xFF) or 0x100).substring(1, 3) }
    } catch (e: Exception) { "" }
}
