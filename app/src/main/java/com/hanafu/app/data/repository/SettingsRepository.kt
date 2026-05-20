package com.hanafu.app.data.repository

import com.hanafu.app.data.local.dao.SettingsDao
import com.hanafu.app.data.local.entity.SettingsEntity

/**
 * 设置数据仓库
 * 仅保留主题和用户协议配置
 */
class SettingsRepository(
    private val settingsDao: SettingsDao
) {

    companion object {
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_AGREEMENT_ACCEPTED = "agreement_accepted"

        const val KEY_CHARACTER_NAME = "character_name"
        const val KEY_CHARACTER_PERSONA = "character_persona"
        const val KEY_CHARACTER_GREETING = "character_greeting"

        val DEFAULT_CHARACTER_NAME = "花谱"
        val DEFAULT_CHARACTER_PERSONA = "你叫花谱，是一个温柔体贴的AI情感陪伴伙伴。你说话温和亲切、善解人意，总是耐心倾听用户的心事，给予温暖贴心的回应和支持。你擅长用柔和的语言与用户交流，偶尔会使用可爱的语气词。"
        val DEFAULT_CHARACTER_GREETING = "你好呀~我是花谱，你的AI情感陪伴伙伴🌸 今天有什么想和我聊聊的吗？无论是开心的事、烦恼的心事，还是随便说说话，我都愿意听你说~"
    }

    // ===== 主题配置 =====

    suspend fun getThemeMode(): String {
        return settingsDao.getSettingValue(KEY_THEME_MODE) ?: "light"
    }

    suspend fun saveThemeMode(mode: String) {
        settingsDao.insertOrUpdateSetting(SettingsEntity(KEY_THEME_MODE, mode))
    }

    // ===== 用户协议 =====

    suspend fun isAgreementAccepted(): Boolean {
        return settingsDao.getSettingValue(KEY_AGREEMENT_ACCEPTED) == "true"
    }

    suspend fun acceptAgreement() {
        settingsDao.insertOrUpdateSetting(SettingsEntity(KEY_AGREEMENT_ACCEPTED, "true"))
    }

    // ===== 角色设定 =====

    suspend fun getCharacterName(): String {
        return settingsDao.getSettingValue(KEY_CHARACTER_NAME) ?: DEFAULT_CHARACTER_NAME
    }

    suspend fun saveCharacterName(name: String) {
        settingsDao.insertOrUpdateSetting(SettingsEntity(KEY_CHARACTER_NAME, name))
    }

    suspend fun getCharacterPersona(): String {
        return settingsDao.getSettingValue(KEY_CHARACTER_PERSONA) ?: DEFAULT_CHARACTER_PERSONA
    }

    suspend fun saveCharacterPersona(persona: String) {
        settingsDao.insertOrUpdateSetting(SettingsEntity(KEY_CHARACTER_PERSONA, persona))
    }

    suspend fun getCharacterGreeting(): String {
        return settingsDao.getSettingValue(KEY_CHARACTER_GREETING) ?: DEFAULT_CHARACTER_GREETING
    }

    suspend fun saveCharacterGreeting(greeting: String) {
        settingsDao.insertOrUpdateSetting(SettingsEntity(KEY_CHARACTER_GREETING, greeting))
    }

    suspend fun saveCharacterConfig(name: String, persona: String, greeting: String) {
        saveCharacterName(name)
        saveCharacterPersona(persona)
        saveCharacterGreeting(greeting)
    }
}
