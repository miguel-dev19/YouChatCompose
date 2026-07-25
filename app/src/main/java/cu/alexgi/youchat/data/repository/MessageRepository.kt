package cu.alexgi.youchat.data.repository

import cu.alexgi.youchat.data.local.YouChatPreferences
import cu.alexgi.youchat.data.local.dao.ChatDao
import cu.alexgi.youchat.data.local.dao.ContactDao
import cu.alexgi.youchat.data.local.dao.MessageDao
import cu.alexgi.youchat.data.local.entity.ChatEntity
import cu.alexgi.youchat.data.local.entity.ContactEntity
import cu.alexgi.youchat.data.local.entity.MessageEntity
import cu.alexgi.youchat.data.remote.MailClient
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val chatDao: ChatDao, private val contactDao: ContactDao,
    private val messageDao: MessageDao, private val mailClient: MailClient,
    private val preferences: YouChatPreferences
) {
    suspend fun sendTextMessage(to: String, text: String, replyToId: String = ""): Boolean {
        val prefs = preferences.preferences.first()
        val fechaEntera = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(Date())
        val id = "YCchat${to}${fechaEntera}"
        val h = (fechaEntera[8]-'0')*10+(fechaEntera[9]-'0')
        val m = "${fechaEntera[10]}${fechaEntera[11]}"
        val hora = when { h==0 -> "12:$m am"; h>12 -> "${h-12}:$m pm"; h==12 -> "12:$m pm"; else -> "$h:$m am" }
        val fecha = "${fechaEntera[6]}${fechaEntera[7]}/${fechaEntera[4]}${fechaEntera[5]}/${fechaEntera[0]}${fechaEntera[1]}${fechaEntera[2]}${fechaEntera[3]}"

        val chat = ChatEntity(id=id, tipoMensaje=if(replyToId.isEmpty())2 else 6, estado=1, correo=to, mensaje=text, rutaDato="", hora=hora, fecha=fecha, idMsgResp=replyToId, emisor=prefs.correo, reenviado=false, orden=fechaEntera)
        chatDao.insertMessage(chat)

        val existing = messageDao.getChat(to)
        if (existing != null) messageDao.insertChat(existing.copy(ultMsgTipo=chat.tipoMensaje, ultMsgTexto=text, ultMsgEstado=1, ultMsgOrden=fechaEntera))
        else messageDao.insertChat(MessageEntity(correo=to, ultMsgTipo=chat.tipoMensaje, ultMsgTexto=text, ultMsgEstado=1, ultMsgOrden=fechaEntera))

        if (contactDao.getContact(to) == null) contactDao.insertContact(ContactEntity(correo=to, nombrePersonal=to, usaYouchat=true))

        return mailClient.sendChatMessage(to=to, mensaje=text, id=id, tipoMensaje=chat.tipoMensaje, hora=hora, fecha=fecha, idMsgResp=replyToId).also { s ->
            if (s) chatDao.updateEstado(id, 3) else chatDao.updateEstado(id, 2)
        }
    }
}
