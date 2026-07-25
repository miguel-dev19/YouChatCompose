package cu.alexgi.youchat.core.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import cu.alexgi.youchat.MainActivity
import cu.alexgi.youchat.R
import cu.alexgi.youchat.data.local.dao.ChatDao
import cu.alexgi.youchat.data.local.dao.ContactDao
import cu.alexgi.youchat.data.local.dao.MessageDao
import cu.alexgi.youchat.data.local.entity.ChatEntity
import cu.alexgi.youchat.data.local.entity.ContactEntity
import cu.alexgi.youchat.data.local.entity.MessageEntity
import cu.alexgi.youchat.data.remote.ImapClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChatService : Service() {

    @Inject lateinit var imapClient: ImapClient
    @Inject lateinit var chatDao: ChatDao
    @Inject lateinit var contactDao: ContactDao
    @Inject lateinit var messageDao: MessageDao

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isRunning = false
    private val CHANNEL_ID = "youchat_foreground"
    private val NOTIFICATION_ID = 7327

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            isRunning = true
            startForeground(NOTIFICATION_ID, createNotification())
            startPolling()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startPolling() {
        scope.launch {
            while (isRunning) {
                try {
                    val messages = imapClient.checkNewMessages()
                    for (msg in messages) {
                        processIncomingMessage(msg)
                    }
                } catch (e: Exception) {
                    Log.e("ChatService", "Error polling", e)
                }
                delay(5000) // Cada 5 segundos (como el original)
            }
        }
    }

    private suspend fun processIncomingMessage(msg: IncomingMessage) {
        try {
            // Verificar si ya existe
            val existing = chatDao.getMessageById(msg.id)
            if (existing != null) return

            // Guardar mensaje
            val chat = ChatEntity(
                id = msg.id,
                tipoMensaje = if (msg.tipoMensaje % 2 == 1) msg.tipoMensaje + 1 else msg.tipoMensaje,
                estado = 4, // RECIBIDO
                correo = msg.from,
                mensaje = msg.mensaje,
                rutaDato = "",
                hora = msg.hora,
                fecha = msg.fecha,
                idMsgResp = msg.idMsgResp,
                emisor = msg.from,
                reenviado = msg.reenviado,
                orden = msg.orden,
                idMensaje = msg.messageId,
                peso = msg.size,
                estaDescargado = true,
                blurhash = null
            )
            chatDao.insertMessage(chat)

            // Actualizar lista de chats
            val existingChat = messageDao.getChat(msg.from)
            if (existingChat != null) {
                messageDao.insertChat(existingChat.copy(
                    ultMsgTipo = chat.tipoMensaje,
                    ultMsgTexto = msg.mensaje,
                    ultMsgEstado = 4,
                    ultMsgOrden = msg.orden,
                    cantMsg = existingChat.cantMsg + 1
                ))
            } else {
                messageDao.insertChat(MessageEntity(
                    correo = msg.from,
                    cantMsg = 1,
                    ultMsgTipo = chat.tipoMensaje,
                    ultMsgTexto = msg.mensaje,
                    ultMsgEstado = 4,
                    ultMsgOrden = msg.orden
                ))
            }

            // Asegurar contacto
            if (contactDao.getContact(msg.from) == null) {
                contactDao.insertContact(ContactEntity(
                    correo = msg.from,
                    nombrePersonal = msg.from,
                    usaYouchat = true
                ))
            }

            // Notificación
            showNotification(msg.from, msg.mensaje)

        } catch (e: Exception) {
            Log.e("ChatService", "Error processing", e)
        }
    }

    private fun showNotification(from: String, message: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("openChat", from)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(from)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("YouChat")
            .setContentText("Servicio en segundo plano")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "YouChat", NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Servicio de mensajería"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        isRunning = false
        scope.cancel()
        imapClient.disconnect()
        super.onDestroy()
    }
}
