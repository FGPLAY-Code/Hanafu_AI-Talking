package com.hanafu.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.hanafu.app.model.ThemeMode
import com.hanafu.app.ui.theme.DarkBackground
import com.hanafu.app.ui.theme.DarkSurfaceVariant
import com.hanafu.app.ui.theme.LightBackground
import com.hanafu.app.ui.theme.LightSurfaceVariant

/**
 * 渐变背景组件
 * Light: 白 → 淡粉 从上到下
 * Dark: 深灰紫 → 暗粉 从上到下
 */
@Composable
fun GradientBackground(
    themeMode: ThemeMode,
    modifier: Modifier = Modifier
) {
    val topColor: Color
    val bottomColor: Color

    when (themeMode) {
        ThemeMode.LIGHT -> {
            topColor = LightBackground
            bottomColor = LightSurfaceVariant
        }
        ThemeMode.DARK -> {
            topColor = DarkBackground
            bottomColor = DarkSurfaceVariant
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val gradient = Brush.verticalGradient(
            colors = listOf(topColor, bottomColor),
            startY = 0f,
            endY = size.height
        )
        drawRect(
            brush = gradient,
            size = size
        )
    }
}
