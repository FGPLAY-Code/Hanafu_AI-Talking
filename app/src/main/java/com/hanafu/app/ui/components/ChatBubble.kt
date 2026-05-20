package com.hanafu.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hanafu.app.model.MessageRole
import com.hanafu.app.ui.theme.BubbleAiBorder
import com.hanafu.app.ui.theme.BubbleUserEnd
import com.hanafu.app.ui.theme.BubbleUserStart
import com.hanafu.app.ui.theme.DarkOnSurface
import com.hanafu.app.ui.theme.DarkSurface
import com.hanafu.app.ui.theme.LightOnBackground

/**
 * 聊天气泡组件
 * 用户气泡：粉紫渐变
 * AI气泡：白色/深色 + 淡粉边框
 */
@Composable
fun ChatBubble(
    content: String,
    role: MessageRole,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val isUser = role == MessageRole.USER

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .shadow(
                    elevation = if (isUser) 4.dp else 2.dp,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    ),
                    ambientColor = if (isUser) BubbleUserStart.copy(alpha = 0.3f)
                    else Color.Black.copy(alpha = 0.1f)
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .then(
                    if (isUser) {
                        Modifier.background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(BubbleUserStart, BubbleUserEnd)
                            )
                        )
                    } else {
                        Modifier.background(
                            color = if (isDarkTheme) DarkSurface else Color.White,
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 16.dp
                            )
                        )
                    }
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isUser) Color.White
                else if (isDarkTheme) DarkOnSurface
                else LightOnBackground
            )
        }
    }
}
