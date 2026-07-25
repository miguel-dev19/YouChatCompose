package cu.alexgi.youchat.core.di

import android.content.Context
import androidx.room.Room
import cu.alexgi.youchat.data.local.YouChatDatabase
import cu.alexgi.youchat.data.local.dao.ChatDao
import cu.alexgi.youchat.data.local.dao.ContactDao
import cu.alexgi.youchat.data.local.dao.MessageDao
import cu.alexgi.youchat.data.remote.CryptoManager
import cu.alexgi.youchat.data.remote.ImapClient
import cu.alexgi.youchat.data.remote.MailClient
import cu.alexgi.youchat.data.repository.MessageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): YouChatDatabase {
        return Room.databaseBuilder(context, YouChatDatabase::class.java, "bd_youchat")
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides fun provideContactDao(db: YouChatDatabase): ContactDao = db.contactDao()
    @Provides fun provideChatDao(db: YouChatDatabase): ChatDao = db.chatDao()
    @Provides fun provideMessageDao(db: YouChatDatabase): MessageDao = db.messageDao()
    
    @Provides @Singleton fun provideCryptoManager(): CryptoManager = CryptoManager()
    @Provides @Singleton fun provideImapClient(imap: ImapClient): ImapClient = imap
}
