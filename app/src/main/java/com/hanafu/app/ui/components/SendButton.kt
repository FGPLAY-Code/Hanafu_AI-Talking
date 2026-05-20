package com.hanafu.app.ui.components

import com.hanafu.app.R
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hanafu.app.ui.theme.BubbleUserEnd
import com.hanafu.app.ui.theme.BubbleUserStart
import com.hanafu.app.ui.theme.SendButtonGlow

/**
 * 粉紫渐变发送按钮
 * 带微扩散光晕动画
 */
@Composable
fun SendButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        modifier = modifier
            .size(48.dp)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = CircleShape,
                ambientColor = SendButtonGlow,
                spotColor = SendButtonGlow
            )
            .scale(if (enabled) glowScale else 1.0f)
            .clip(CircleShape)
            .background(
                brush = if (enabled) {
                    Brush.horizontalGradient(
                        colors = listOf(BubbleUserStart, BubbleUserEnd)
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(Color.Gray, Color.LightGray)
                    )
                }
            )
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = stringResource(R.string.send_desc),
            tint = Color.White,
            modifier = Modifier.padding(10.dp)
        )
    }
}
