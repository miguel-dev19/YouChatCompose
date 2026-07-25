package cu.alexgi.youchat.data.local.dao

import androidx.room.*
import cu.alexgi.youchat.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM usuarios ORDER BY ult_msg_orden DESC")
    fun getAllChats(): Flow<List<MessageEntity>>
    
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun getChat(correo: String): MessageEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: MessageEntity)
    
    @Query("UPDATE usuarios SET cant_msg = 0 WHERE correo = :correo")
    suspend fun markAsRead(correo: String)
}
