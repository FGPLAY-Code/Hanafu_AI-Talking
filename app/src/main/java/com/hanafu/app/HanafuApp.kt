package com.hanafu.app

import android.app.Application
import com.hanafu.app.di.AppModule
import com.hanafu.app.util.LanguageManager

/**
 * 花谱 Application 类
 */
class HanafuApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // 初始化依赖注入
        AppModule.initialize(this)

        // 应用保存的语言设置
        LanguageManager.applyLanguage(this)
    }
}
