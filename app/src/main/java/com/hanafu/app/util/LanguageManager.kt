package com.hanafu.app.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * 语言管理工具
 * 支持英文、中文、日文三语切换
 */
object LanguageManager {

    private const val PREFS_NAME = "hanafu_language"
    private const val KEY_LANGUAGE = "app_language"

    enum class AppLanguage(val code: String, val displayName: String) {
        ENGLISH("en", "English"),
        CHINESE("zh", "中文"),
        JAPANESE("ja", "日本語")
    }

    /**
     * 获取当前语言
     */
    fun getCurrentLanguage(context: Context): AppLanguage {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val code = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        return when (code) {
            "zh" -> AppLanguage.CHINESE
            "ja" -> AppLanguage.JAPANESE
            else -> AppLanguage.ENGLISH
        }
    }

    /**
     * 保存并应用语言
     */
    fun setLanguage(context: Context, language: AppLanguage) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
        applyLanguage(context)
    }

    /**
     * 在当前 Activity 上应用语言（更新 Configuration）
     */
    fun applyLanguage(context: Context) {
        val language = getCurrentLanguage(context)
        val locale = Locale(language.code)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    /**
     * 获取语言对应的 Context（用于 attachBaseContext）
     */
    fun getLocalizedContext(context: Context): Context {
        val language = getCurrentLanguage(context)
        val locale = Locale(language.code)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}
