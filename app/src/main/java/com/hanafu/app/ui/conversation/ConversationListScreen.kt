package com.hanafu.app.ui.conversation

import com.hanafu.app.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hanafu.app.di.AppModule
import com.hanafu.app.model.Conversation
import com.hanafu.app.model.ThemeMode
import com.hanafu.app.ui.chat.CharacterDialog
import com.hanafu.app.ui.components.GradientBackground
import com.hanafu.app.ui.theme.AccentPink
import com.hanafu.app.ui.theme.DarkBackground
import com.hanafu.app.ui.theme.DarkOnBackground
import com.hanafu.app.ui.theme.DarkSurface
import com.hanafu.app.ui.theme.LightBackground
import com.hanafu.app.ui.theme.LightSurface
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    themeMode: ThemeMode,
    onConversationClick: (Long) -> Unit,
    onNewConversation: (convId: Long) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val isDarkTheme = themeMode == ThemeMode.DARK
    val chatRepository = remember { AppModule.getChatRepository() }
    val settingsRepository = remember { AppModule.getSettingsRepository() }
    val conversations by chatRepository.getAllConversations().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var deleteTarget by remember { mutableStateOf<Conversation?>(null) }
    var showCharacterDialog by remember { mutableStateOf(false) }
    var dialogName by remember { mutableStateOf("") }
    var dialogPersona by remember { mutableStateOf("") }
    var dialogGreeting by remember { mutableStateOf("") }
    var dialogSessionName by remember { mutableStateOf("") }
    val defaultConvTitle = stringResource(R.string.conv_new_default_title)

    // 新建会话时的人设编辑弹窗
    if (showCharacterDialog) {
        CharacterDialog(
            currentName = dialogName,
            currentPersona = dialogPersona,
            currentGreeting = dialogGreeting,
            showSessionName = true,
            sessionName = dialogSessionName,
            onSessionNameChange = { dialogSessionName = it },
            onSave = { name, persona, greeting ->
                scope.launch {
                    settingsRepository.saveCharacterConfig(name, persona, greeting)
                    val title = dialogSessionName.ifBlank { defaultConvTitle }
                    val convId = chatRepository.createConversation(title = title)
                    showCharacterDialog = false
                    onNewConversation(convId)
                }
            },
            onDismiss = { showCharacterDialog = false }
        )
    }

    // 删除确认对话框
    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(stringResource(R.string.conv_delete_title)) },
            text = { Text(stringResource(R.string.conv_delete_message, deleteTarget?.title ?: "")) },
            confirmButton = {
                TextButton(onClick = {
                    deleteTarget?.let { conv ->
                        scope.launch {
                            chatRepository.deleteAllMessagesInConversation(conv.id)
                            chatRepository.deleteConversation(conv.id)
                        }
                    }
                    deleteTarget = null
                }) {
                    Text(stringResource(R.string.conv_delete_confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text(stringResource(R.string.conv_delete_cancel))
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(themeMode = themeMode)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.conv_list_title),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.conv_settings_desc)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isDarkTheme) DarkBackground.copy(alpha = 0.7f)
                        else LightBackground.copy(alpha = 0.7f),
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            dialogName = settingsRepository.getCharacterName()
                            dialogPersona = settingsRepository.getCharacterPersona()
                            dialogGreeting = settingsRepository.getCharacterGreeting()
                            showCharacterDialog = true
                        }
                    },
                    containerColor = AccentPink,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.conv_new_desc))
                }
            }
        ) { paddingValues ->
            if (conversations.isEmpty()) {
                // 空状态
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🌸",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.conv_empty_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.conv_empty_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = conversations,
                        key = { it.id }
                    ) { conversation ->
                        ConversationCard(
                            conversation = conversation,
                            onClick = { onConversationClick(conversation.id) },
                            onDelete = { deleteTarget = conversation },
                            isDarkTheme = isDarkTheme
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationCard(
    conversation: Conversation,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    isDarkTheme: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) DarkSurface.copy(alpha = 0.85f)
            else LightSurface.copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conversation.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isDarkTheme) DarkOnBackground else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatTimestamp(conversation.updatedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.conv_message_count, conversation.messageCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.conv_delete_desc),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
