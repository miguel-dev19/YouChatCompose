package cu.alexgi.youchat.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import cu.alexgi.youchat.data.local.dao.ContactDao
import cu.alexgi.youchat.data.local.dao.ChatDao
import cu.alexgi.youchat.data.local.dao.MessageDao
import cu.alexgi.youchat.data.local.entity.ContactEntity
import cu.alexgi.youchat.data.local.entity.ChatEntity
import cu.alexgi.youchat.data.local.entity.MessageEntity

@Database(
    entities = [ContactEntity::class, ChatEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class YouChatDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}
