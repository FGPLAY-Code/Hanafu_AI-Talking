package com.hanafu.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hanafu.app.di.AppModule
import com.hanafu.app.ui.agreement.UserAgreementScreen
import com.hanafu.app.ui.chat.ChatScreen
import com.hanafu.app.ui.conversation.ConversationListScreen
import com.hanafu.app.ui.settings.SettingsScreen
import com.hanafu.app.ui.splash.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    navController: NavHostController,
    themeMode: com.hanafu.app.model.ThemeMode,
    onThemeToggle: (com.hanafu.app.model.ThemeMode) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Agreement.route,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable(Screen.Agreement.route) {
            val settingsRepo = remember { AppModule.getSettingsRepository() }
            val scope = rememberCoroutineScope()
            var agreed by remember { mutableStateOf<Boolean?>(null) }

            LaunchedEffect(Unit) {
                agreed = settingsRepo.isAgreementAccepted()
            }

            when (agreed) {
                null -> { /* 加载中，不渲染任何内容 */ }
                true -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Splash.route) {
                            popUpTo(Screen.Agreement.route) { inclusive = true }
                        }
                    }
                }
                false -> {
                    UserAgreementScreen(
                        onAgreed = {
                            scope.launch {
                                settingsRepo.acceptAgreement()
                                navController.navigate(Screen.Splash.route) {
                                    popUpTo(Screen.Agreement.route) { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }
        }

        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToChat = {
                    navController.navigate(Screen.ConversationList.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ConversationList.route) {
            ConversationListScreen(
                themeMode = themeMode,
                onConversationClick = { conversationId ->
                    navController.navigate(Screen.Chat.createRoute(conversationId))
                },
                onNewConversation = { convId ->
                    navController.navigate(Screen.Chat.createRoute(convId)) {
                        popUpTo(Screen.ConversationList.route) { inclusive = false }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("conversationId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getLong("conversationId") ?: -1L
            ChatScreen(
                conversationId = conversationId,
                themeMode = themeMode,
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                themeMode = themeMode,
                onThemeToggle = onThemeToggle,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
