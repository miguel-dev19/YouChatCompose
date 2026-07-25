package cu.alexgi.youchat.data.local.dao

import androidx.room.*
import cu.alexgi.youchat.data.local.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats WHERE correo = :correo ORDER BY orden DESC LIMIT :limite")
    fun getMessages(correo: String, limite: Int = 50): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE id = :id LIMIT 1")
    suspend fun getMessageById(id: String): ChatEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(chat: ChatEntity)
    
    @Query("UPDATE chats SET estado = :estado WHERE id = :id")
    suspend fun updateEstado(id: String, estado: Int)
    
    @Query("UPDATE chats SET ruta_dato = :ruta, esta_descargado = 1 WHERE id = :id")
    suspend fun updateDescargado(id: String, ruta: String)
    
    @Query("DELETE FROM chats WHERE id = :id")
    suspend fun deleteMessage(id: String)
    
    @Query("DELETE FROM chats WHERE correo = :correo")
    suspend fun deleteAllMessages(correo: String)
}
