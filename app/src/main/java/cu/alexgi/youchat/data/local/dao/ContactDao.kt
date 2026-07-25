package cu.alexgi.youchat.data.local.dao

import androidx.room.*
import cu.alexgi.youchat.data.local.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contactos WHERE tipo_contacto = 1 OR tipo_contacto = 2 ORDER BY usa_youchat DESC, nombre_personal ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contactos WHERE correo = :correo LIMIT 1")
    suspend fun getContact(correo: String): ContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)

    @Query("DELETE FROM contactos WHERE correo = :correo")
    suspend fun deleteContactByEmail(correo: String)

    @Query("UPDATE contactos SET silenciado = :silenciado WHERE correo = :correo")
    suspend fun updateSilenciado(correo: String, silenciado: Boolean)

    @Query("UPDATE contactos SET bloqueado = :bloqueado WHERE correo = :correo")
    suspend fun updateBloqueado(correo: String, bloqueado: Boolean)

    @Query("UPDATE contactos SET nombre_personal = :nombre WHERE correo = :correo")
    suspend fun updateNombre(correo: String, nombre: String)

    @Query("SELECT * FROM contactos WHERE bloqueado = 1")
    fun getBloqueados(): Flow<List<ContactEntity>>
}
