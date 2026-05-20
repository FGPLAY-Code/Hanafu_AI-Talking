package com.hanafu.app.ui.splash

import com.hanafu.app.R
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hanafu.app.ui.theme.PinkGradientEnd
import com.hanafu.app.ui.theme.PinkGradientStart
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * 启动页
 * 全屏粉色渐变 + 花瓣飘落粒子动画
 */
@Composable
fun SplashScreen(
    onNavigateToChat: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    var navigate by remember { mutableStateOf(false) }

    // 花瓣粒子数据
    val petals = remember {
        List(20) {
            Petal(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -1f,
                size = Random.nextFloat() * 20f + 10f,
                speed = Random.nextFloat() * 0.005f + 0.002f,
                drift = Random.nextFloat() * 0.003f - 0.0015f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 2f - 1f,
                alpha = Random.nextFloat() * 0.5f + 0.3f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "petal")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        delay(500)
        showContent = true
        delay(2000)
        navigate = true
    }

    LaunchedEffect(navigate) {
        if (navigate) {
            onNavigateToChat()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PinkGradientStart, PinkGradientEnd)
                )
            )
    ) {
        // 花瓣动画
        Canvas(modifier = Modifier.fillMaxSize()) {
            petals.forEach { petal ->
                val currentY = (petal.y + progress * petal.speed * 1000) % 1.2f - 0.1f
                val currentX = petal.x + sin(currentY * 10) * 0.05f + petal.drift * progress * 1000
                val currentRotation = petal.rotation + progress * petal.rotationSpeed * 100

                drawPath(
                    path = createPetalPath(
                        centerX = currentX * size.width,
                        centerY = currentY * size.height,
                        size = petal.size
                    ),
                    color = Color.White.copy(alpha = petal.alpha),
                    style = Stroke(width = 2f)
                )
            }
        }

        // 标题
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.splash_title),
                style = MaterialTheme.typography.displayLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.splash_name),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.splash_name),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

private data class Petal(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val drift: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val alpha: Float
)

private fun createPetalPath(
    centerX: Float,
    centerY: Float,
    size: Float
): Path {
    return Path().apply {
        moveTo(centerX, centerY - size / 2)
        cubicTo(
            centerX + size / 2, centerY - size / 4,
            centerX + size / 2, centerY + size / 4,
            centerX, centerY + size / 2
        )
        cubicTo(
            centerX - size / 2, centerY + size / 4,
            centerX - size / 2, centerY - size / 4,
            centerX, centerY - size / 2
        )
        close()
    }
}
