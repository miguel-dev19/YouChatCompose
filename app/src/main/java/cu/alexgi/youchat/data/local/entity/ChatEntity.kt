package cu.alexgi.youchat.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "tipo_mensaje") val tipoMensaje: Int,
    val estado: Int,
    val correo: String,
    val mensaje: String,
    @ColumnInfo(name = "ruta_dato") val rutaDato: String,
    val hora: String,
    val fecha: String,
    @ColumnInfo(name = "id_msg_resp") val idMsgResp: String,
    val emisor: String,
    val reenviado: Boolean,
    val orden: String,
    val editado: Boolean = false,
    @ColumnInfo(name = "id_mensaje") val idMensaje: String = "",
    val peso: Int = 0,
    @ColumnInfo(name = "esta_descargado") val estaDescargado: Boolean = true,
    val blurhash: String? = null
)
