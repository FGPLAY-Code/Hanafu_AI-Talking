package com.hanafu.app.ui.navigation

/**
 * 导航路由定义
 */
sealed class Screen(val route: String) {
    data object Agreement : Screen("agreement")
    data object Splash : Screen("splash")
    data object ConversationList : Screen("conversation_list")
    data object Chat : Screen("chat/{conversationId}") {
        fun createRoute(conversationId: Long = -1) = "chat/$conversationId"
    }
    data object Settings : Screen("settings")
}
