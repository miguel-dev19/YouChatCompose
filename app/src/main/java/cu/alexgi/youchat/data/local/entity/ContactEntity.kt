package cu.alexgi.youchat.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contactos")
data class ContactEntity(
    @PrimaryKey val correo: String,
    val alias: String = "",
    @ColumnInfo(name = "nombre_personal") val nombrePersonal: String = "",
    @ColumnInfo(name = "tipo_contacto") val tipoContacto: Int = 1,
    @ColumnInfo(name = "ruta_img") val rutaImg: String = "",
    val info: String = "",
    val telefono: String = "",
    val genero: String = "",
    val provincia: String = "",
    @ColumnInfo(name = "fecha_nac") val fechaNac: String = "",
    @ColumnInfo(name = "usa_youchat") val usaYouchat: Boolean = false,
    val silenciado: Boolean = false,
    val bloqueado: Boolean = false,
    @ColumnInfo(name = "cant_seguidores") val cantSeguidores: Int = 0
)
