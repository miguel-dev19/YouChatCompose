package cu.alexgi.youchat.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class MessageEntity(
    @PrimaryKey val correo: String,
    val anclado: Boolean = false,
    @ColumnInfo(name = "cant_msg") val cantMsg: Int = 0,
    @ColumnInfo(name = "ult_msg_tipo") val ultMsgTipo: Int = 0,
    @ColumnInfo(name = "ult_msg_texto") val ultMsgTexto: String = "",
    @ColumnInfo(name = "ult_msg_estado") val ultMsgEstado: Int = 0,
    @ColumnInfo(name = "ult_msg_orden") val ultMsgOrden: String = ""
)
