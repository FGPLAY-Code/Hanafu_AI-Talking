package com.hanafu.app.ui.chat

import android.net.Uri
import com.hanafu.app.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hanafu.app.di.AppModule
import com.hanafu.app.model.ThemeMode
import com.hanafu.app.ui.components.ChatBubble
import com.hanafu.app.ui.components.GradientBackground
import com.hanafu.app.ui.components.PulsingIndicator
import com.hanafu.app.ui.components.SendButton
import com.hanafu.app.ui.theme.AccentPink
import com.hanafu.app.ui.theme.BubbleUserEnd
import com.hanafu.app.ui.theme.BubbleUserStart
import com.hanafu.app.ui.theme.DarkBackground
import com.hanafu.app.ui.theme.LightBackground
import com.hanafu.app.ui.theme.SendButtonGlow
import com.hanafu.app.util.OcrHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: Long,
    themeMode: ThemeMode,
    onNavigateToSettings: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = viewModel(
        factory = ChatViewModel.Factory(
            chatRepository = AppModule.getChatRepository(),
            settingsRepository = AppModule.getSettingsRepository(),
            conversationId = conversationId
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val isDarkTheme = themeMode == ThemeMode.DARK
    var showCharacterDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isOcrLoading by remember { mutableStateOf(false) }

    // 图片选择器
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            isOcrLoading = true
            scope.launch {
                try {
                    val text = OcrHelper.recognizeText(context, it)
                    if (inputText.isNotBlank()) inputText += "\n"
                    inputText += text
                } catch (e: Exception) {
                    // OCR 失败，清空选择
                }
                isOcrLoading = false
                selectedImageUri = null
            }
        }
    }

    // 人设编辑弹窗
    if (showCharacterDialog) {
        CharacterDialog(
            currentName = uiState.characterName,
            currentPersona = uiState.characterPersona,
            currentGreeting = uiState.characterGreeting,
            onSave = { name, persona, greeting ->
                viewModel.updateCharacter(name, persona, greeting)
                showCharacterDialog = false
            },
            onDismiss = { showCharacterDialog = false }
        )
    }

    // 自动滚动到底部
    LaunchedEffect(uiState.messages.size, uiState.streamingContent) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 渐变背景
        GradientBackground(themeMode = themeMode)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column(
                            modifier = Modifier.clickable { showCharacterDialog = true }
                        ) {
                            Text(
                                text = uiState.currentConversation?.title ?: stringResource(R.string.chat_title_default),
                                style = MaterialTheme.typography.titleLarge
                            )
                            if (uiState.characterName != "花谱") {
                                Text(
                                    text = uiState.characterName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.chat_back_desc)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.chat_settings_desc)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isDarkTheme) {
                            DarkBackground.copy(alpha = 0.7f)
                        } else {
                            LightBackground.copy(alpha = 0.7f)
                        },
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            bottomBar = {
                // 输入区域
                ChatInputBar(
                    inputText = inputText,
                    onInputChange = { inputText = it },
                    onSend = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    isEnabled = !uiState.isLoading,
                    isDarkTheme = isDarkTheme,
                    isOcrLoading = isOcrLoading,
                    onPickImage = { imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 消息列表
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 空状态
                    if (uiState.messages.isEmpty() && !uiState.isLoading) {
                        item {
                            EmptyChatState(isDarkTheme = isDarkTheme)
                        }
                    }

                    // 消息列表
                    items(
                        items = uiState.messages,
                        key = { it.id }
                    ) { message ->
                        ChatBubble(
                            content = message.content,
                            role = message.role,
                            isDarkTheme = isDarkTheme
                        )
                    }

                    // 流式响应中的内容
                    if (uiState.isStreaming && uiState.streamingContent.isNotEmpty()) {
                        item {
                            ChatBubble(
                                content = uiState.streamingContent,
                                role = com.hanafu.app.model.MessageRole.ASSISTANT,
                                isDarkTheme = isDarkTheme
                            )
                        }
                    }

                    // 加载指示器 + "思考中"
                    if (uiState.isLoading && uiState.streamingContent.isEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                PulsingIndicator()
                                Text(
                                    text = stringResource(R.string.chat_thinking),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                            colors = listOf(
                                                AccentPink,
                                                Color.White.copy(alpha = 0.7f)
                                            )
                                        )
                                    )
                                )
                            }
                        }
                    }

                    // 底部间距
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // 错误提示
                if (uiState.error != null) {
                    Text(
                        text = uiState.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    isEnabled: Boolean,
    isDarkTheme: Boolean,
    isOcrLoading: Boolean = false,
    onPickImage: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isDarkTheme) {
                    DarkBackground.copy(alpha = 0.8f)
                } else {
                    LightBackground.copy(alpha = 0.8f)
                }
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 加号按钮（粉色渐变，和发送按钮对齐）
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(
                    elevation = if (!isOcrLoading) 8.dp else 0.dp,
                    shape = CircleShape,
                    ambientColor = SendButtonGlow,
                    spotColor = SendButtonGlow
                )
                .scale(if (!isOcrLoading) 1.0f else 1.0f)
                .clip(CircleShape)
                .background(
                    brush = if (!isOcrLoading) {
                        Brush.horizontalGradient(
                            colors = listOf(BubbleUserStart, BubbleUserEnd)
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(Color.Gray, Color.LightGray)
                        )
                    }
                )
                .clickable(enabled = !isOcrLoading) { onPickImage() },
            contentAlignment = Alignment.Center
        ) {
            if (isOcrLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "识别图片文字",
                    tint = Color.White,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    stringResource(R.string.chat_input_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentPink,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedContainerColor = if (isDarkTheme) {
                    Color(0xFF2A1F25)
                } else {
                    Color.White
                },
                unfocusedContainerColor = if (isDarkTheme) {
                    Color(0xFF2A1F25)
                } else {
                    Color.White
                },
                cursorColor = AccentPink
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSend() }),
            singleLine = true,
            maxLines = 1
        )

        Spacer(modifier = Modifier.width(8.dp))

        SendButton(
            enabled = isEnabled && inputText.isNotBlank(),
            onClick = onSend
        )
    }
}

@Composable
private fun EmptyChatState(isDarkTheme: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.splash_title),
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.chat_empty_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.chat_empty_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
