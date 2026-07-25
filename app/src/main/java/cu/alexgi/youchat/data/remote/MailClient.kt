package cu.alexgi.youchat.data.remote

import android.util.Log
import com.sun.mail.smtp.SMTPTransport
import cu.alexgi.youchat.data.local.YouChatPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.*
import javax.inject.Inject
import javax.inject.Singleton

object MessageCategory { const val CHAT = "1"; const val CHAT_ACT = "3"; const val SOL_SEGUIR = "7" }
object MailHeaders { const val YOUCHAT = "youchat"; const val KEY_ID = "msg_id"; const val KEY_TIPO = "msg_tipo"; const val PIE_DE_FIRMA = "YouChat" }

@Singleton
class MailClient @Inject constructor(private val preferences: YouChatPreferences, private val cryptoManager: CryptoManager) {
    private var transport: SMTPTransport? = null
    private var session: Session? = null
    private var isConnected = false

    private suspend fun getSession(): Session {
        if (session != null) return session!!
        val prefs = preferences.preferences.first()
        val props = Properties()
        when {
            prefs.correo.endsWith("@nauta.cu") -> { props["mail.smtp.host"] = "smtp.nauta.cu"; props["mail.smtp.port"] = "25" }
            prefs.correo.endsWith("@gmail.com") -> { props["mail.smtp.host"] = "smtp.gmail.com"; props["mail.smtp.port"] = "465" }
            else -> { props["mail.smtp.host"] = "smtp.nauta.cu"; props["mail.smtp.port"] = "25" }
        }
        session = Session.getDefaultInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication() = PasswordAuthentication(prefs.correo, prefs.pass)
        })
        return session!!
    }

    suspend fun sendChatMessage(to: String, mensaje: String, id: String, tipoMensaje: Int = 2, hora: String, fecha: String, idMsgResp: String = "", rutaArchivo: String? = null): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!isConnected) { transport = SMTPTransport(getSession(), URLName("smtp", getSession().properties.getProperty("mail.smtp.host"), getSession().properties.getProperty("mail.smtp.port")?.toInt() ?: 25, null, preferences.preferences.first().correo, preferences.preferences.first().pass)); transport?.connect(); isConnected = true }
            val prefs = preferences.preferences.first()
            val message = MimeMessage(getSession())
            message.setFrom(InternetAddress(prefs.correo))
            message.addRecipient(Message.RecipientType.TO, InternetAddress(to))
            message.subject = MailHeaders.PIE_DE_FIRMA
            message.addHeader(MailHeaders.YOUCHAT, MessageCategory.CHAT)
            message.addHeader(MailHeaders.KEY_ID, id)
            message.addHeader(MailHeaders.KEY_TIPO, tipoMensaje.toString())
            if (rutaArchivo != null && File(rutaArchivo).exists()) {
                val tp = MimeBodyPart(); tp.setText(if (prefs.chatSecurity) cryptoManager.encrypt(mensaje) else mensaje)
                val fp = MimeBodyPart(); fp.dataHandler = DataHandler(FileDataSource(rutaArchivo)); fp.fileName = File(rutaArchivo).name
                val mp = MimeMultipart(); mp.addBodyPart(tp); mp.addBodyPart(fp); message.setContent(mp)
            } else message.setText(if (prefs.chatSecurity) cryptoManager.encrypt(mensaje) else mensaje)
            transport?.sendMessage(message, message.allRecipients)
            true
        } catch (e: Exception) { Log.e("MailClient", "Error", e); false }
    }

    suspend fun verifyCredentials(correo: String, pass: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val props = Properties(); props["mail.smtp.host"] = "smtp.nauta.cu"; props["mail.smtp.port"] = "25"
            val s = Session.getDefaultInstance(props, object : Authenticator() { override fun getPasswordAuthentication() = PasswordAuthentication(correo, pass) })
            val t = SMTPTransport(s, URLName("smtp", "smtp.nauta.cu", 25, null, correo, pass)); t.connect()
            val m = MimeMessage(s); m.setFrom(InternetAddress(correo)); m.addRecipient(Message.RecipientType.TO, InternetAddress(correo))
            m.addHeader("youchat", "youchat"); m.addHeader("msg_id", "YouChat/login/$correo"); m.addHeader("msg_tipo", "2")
            t.sendMessage(m, m.allRecipients); t.close(); true
        } catch (e: Exception) { Log.e("MailClient", "Verify error", e); false }
    }
}
