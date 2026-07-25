package cu.alexgi.youchat.data.remote

import android.util.Log
import com.sun.mail.imap.IMAPFolder
import com.sun.mail.imap.IMAPMessage
import com.sun.mail.imap.IMAPStore
import cu.alexgi.youchat.data.local.YouChatPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMultipart
import javax.mail.search.SubjectTerm
import javax.inject.Inject
import javax.inject.Singleton

data class IncomingMessage(
    val from: String,
    val id: String,
    val categoria: String,
    val tipoMensaje: Int,
    val mensaje: String,
    val hora: String,
    val fecha: String,
    val orden: String,
    val idMsgResp: String,
    val reenviado: Boolean,
    val estaEncriptado: Boolean,
    val messageId: String,
    val size: Int,
    val uid: Long
)

data class IncomingFile(
    val fileName: String,
    val fileSize: Int,
    val inputStream: java.io.InputStream,
    val isEncrypted: Boolean
)

@Singleton
class ImapClient @Inject constructor(
    private val preferences: YouChatPreferences,
    private val cryptoManager: CryptoManager
) {
    private var store: IMAPStore? = null
    private var inbox: IMAPFolder? = null
    private var isConnected = false

    private suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            val prefs = preferences.preferences.first()
            val props = Properties()

            when {
                prefs.correo.endsWith("@nauta.cu") -> {
                    props["mail.store.protocol"] = "imap"
                    props["mail.imap.host"] = "imap.nauta.cu"
                    props["mail.imap.port"] = "143"
                }
                prefs.correo.endsWith("@gmail.com") -> {
                    props["mail.imap.starttls.enable"] = "false"
                    props["mail.imap.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
                    props["mail.imap.socketFactory.fallback"] = "false"
                    props["mail.imap.port"] = "993"
                }
                else -> {
                    props["mail.store.protocol"] = "imap"
                    props["mail.imap.host"] = "imap.nauta.cu"
                    props["mail.imap.port"] = "143"
                }
            }

            val session = Session.getDefaultInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(prefs.correo, prefs.pass)
                }
            })

            store = session.getStore("imap") as IMAPStore
            store?.connect(
                if (prefs.correo.endsWith("@gmail.com")) "imap.gmail.com" else "imap.nauta.cu",
                prefs.correo, prefs.pass
            )

            inbox = store?.getFolder("Inbox") as? IMAPFolder
            inbox?.open(Folder.READ_WRITE)
            isConnected = true
            true
        } catch (e: Exception) {
            Log.e("ImapClient", "Error conectando IMAP", e)
            isConnected = false
            false
        }
    }

    suspend fun checkNewMessages(): List<IncomingMessage> = withContext(Dispatchers.IO) {
        try {
            if (!isConnected || inbox == null || !inbox!!.isOpen) {
                if (!connect()) return@withContext emptyList()
            }

            val messages = inbox!!.search(SubjectTerm("youchat"))
            if (messages.isEmpty()) return@withContext emptyList()

            val result = mutableListOf<IncomingMessage>()
            val sdf = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault())

            for (msg in messages) {
                try {
                    val imapMsg = msg as IMAPMessage
                    val from = imapMsg.from?.firstOrNull()?.toString()?.trim() ?: continue

                    // Headers
                    val headers = imapMsg.allHeaders
                    val id = imapMsg.getHeader("msg_id")?.firstOrNull() ?: continue
                    val tipoStr = imapMsg.getHeader("msg_tipo")?.firstOrNull() ?: "2"
                    val tipo = tipoStr.toIntOrNull() ?: 2
                    val idResp = imapMsg.getHeader("msg_id_resp")?.firstOrNull() ?: ""
                    val reenviado = imapMsg.getHeader("msg_reenviado")?.firstOrNull() == "1"
                    val encriptado = imapMsg.getHeader("ee")?.firstOrNull() == "1"

                    val sentDate = imapMsg.sentDate ?: Date()
                    val horaReal = SimpleDateFormat("yyyyMMddHHmmss").format(sentDate)
                    val fecha = "${horaReal[6]}${horaReal[7]}/${horaReal[4]}${horaReal[5]}/${horaReal[0]}${horaReal[1]}${horaReal[2]}${horaReal[3]}"
                    val h = (horaReal[8]-'0')*10 + (horaReal[9]-'0')
                    val m = "${horaReal[10]}${horaReal[11]}"
                    val hora = when { h==0 -> "12:$m am"; h>12 -> "${h-12}:$m pm"; h==12 -> "12:$m pm"; else -> "$h:$m am" }

                    val orden = sdf.format(Date())
                    val size = imapMsg.size
                    val uid = inbox!!.getUID(imapMsg)
                    val messageId = imapMsg.messageID ?: ""

                    var texto = ""
                    val content = imapMsg.content
                    if (content is String) {
                        texto = if (encriptado) cryptoManager.decrypt(content.trim()) else content.trim()
                    } else if (content is MimeMultipart) {
                        texto = try {
                            val body = content.getBodyPart(0)
                            val t = body.content.toString().trim()
                            if (encriptado) cryptoManager.decrypt(t) else t
                        } catch (e: Exception) { "" }
                    }

                    result.add(IncomingMessage(
                        from = from, id = id, categoria = "1", tipoMensaje = tipo,
                        mensaje = texto, hora = hora, fecha = fecha, orden = orden,
                        idMsgResp = idResp, reenviado = reenviado, estaEncriptado = encriptado,
                        messageId = messageId, size = size, uid = uid
                    ))

                    // Marcar como eliminado
                    imapMsg.flag = Flags(Flags.Flag.DELETED)

                } catch (e: Exception) {
                    Log.e("ImapClient", "Error procesando mensaje", e)
                }
            }

            inbox!!.expunge()
            result
        } catch (e: Exception) {
            Log.e("ImapClient", "Error checking messages", e)
            isConnected = false
            emptyList()
        }
    }

    suspend fun downloadAttachment(messageId: String, uid: Long): IncomingFile? = withContext(Dispatchers.IO) {
        try {
            if (!isConnected) connect()
            val msg = inbox?.getMessageByUID(uid) as? IMAPMessage ?: return@withContext null
            val content = msg.content
            if (content is MimeMultipart && content.count >= 2) {
                val part = content.getBodyPart(1)
                val fileName = part.fileName ?: "file_${System.currentTimeMillis()}"
                val isEnc = msg.getHeader("ee")?.firstOrNull() == "1"
                IncomingFile(fileName, part.size, part.inputStream, isEnc)
            } else null
        } catch (e: Exception) { null }
    }

    suspend fun getInboxCount(): Int = withContext(Dispatchers.IO) {
        try {
            if (!isConnected) connect()
            inbox?.messageCount ?: 0
        } catch (e: Exception) { 0 }
    }

    fun disconnect() {
        try { inbox?.close(true) } catch (e: Exception) {}
        try { store?.close() } catch (e: Exception) {}
        isConnected = false
    }
}
