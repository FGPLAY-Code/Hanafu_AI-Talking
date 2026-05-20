package com.hanafu.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.hanafu.app.model.ThemeMode
import com.hanafu.app.ui.navigation.NavGraph
import com.hanafu.app.ui.theme.HanafuTheme
import com.hanafu.app.util.LanguageManager

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageManager.getLocalizedContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var themeMode by remember { mutableStateOf(ThemeMode.LIGHT) }

            HanafuTheme(
                themeMode = themeMode,
                dynamicColor = false
            ) {
                val navController = rememberNavController()

                NavGraph(
                    navController = navController,
                    themeMode = themeMode,
                    onThemeToggle = { newMode ->
                        themeMode = newMode
                    }
                )
            }
        }
    }
}
