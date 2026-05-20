package com.hanafu.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hanafu.app.data.local.dao.ChatDao
import com.hanafu.app.data.local.dao.ConversationDao
import com.hanafu.app.data.local.dao.SettingsDao
import com.hanafu.app.data.local.entity.ChatMessageEntity
import com.hanafu.app.data.local.entity.ConversationEntity
import com.hanafu.app.data.local.entity.SettingsEntity

@Database(
    entities = [
        ChatMessageEntity::class,
        ConversationEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao
    abstract fun conversationDao(): ConversationDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hanafu_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
