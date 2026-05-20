package com.hanafu.app.di

import android.content.Context
import com.hanafu.app.data.local.AppDatabase
import com.hanafu.app.data.repository.ChatRepository
import com.hanafu.app.data.repository.SettingsRepository

/**
 * 手动依赖注入容器
 */
object AppModule {

    private var database: AppDatabase? = null
    private var chatRepository: ChatRepository? = null
    private var settingsRepository: SettingsRepository? = null

    fun initialize(context: Context) {
        database = AppDatabase.getInstance(context)
        chatRepository = ChatRepository(
            chatDao = database!!.chatDao(),
            conversationDao = database!!.conversationDao()
        )
        settingsRepository = SettingsRepository(
            settingsDao = database!!.settingsDao()
        )
    }

    fun getChatRepository(): ChatRepository {
        return chatRepository ?: throw IllegalStateException("AppModule not initialized")
    }

    fun getSettingsRepository(): SettingsRepository {
        return settingsRepository ?: throw IllegalStateException("AppModule not initialized")
    }
}
