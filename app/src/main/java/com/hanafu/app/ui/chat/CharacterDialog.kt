package com.hanafu.app.ui.chat

import com.hanafu.app.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.hanafu.app.data.repository.SettingsRepository
import com.hanafu.app.ui.theme.AccentPink

@Composable
fun CharacterDialog(
    currentName: String,
    currentPersona: String,
    currentGreeting: String,
    showSessionName: Boolean = false,
    sessionName: String = "",
    onSessionNameChange: (String) -> Unit = {},
    onSave: (name: String, persona: String, greeting: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var persona by remember { mutableStateOf(currentPersona) }
    var greeting by remember { mutableStateOf(currentGreeting) }
    var showConfirm by remember { mutableStateOf(false) }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text(stringResource(R.string.char_confirm_title)) },
            text = { Text(stringResource(R.string.char_confirm_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    onSave(name, persona, greeting)
                }) {
                    Text(stringResource(R.string.char_confirm_button), color = AccentPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text(stringResource(R.string.char_cancel_button))
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.char_dialog_title), style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // 会话名称（新建会话时显示）
                if (showSessionName) {
                    Text(
                        text = stringResource(R.string.char_session_name),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = sessionName,
                        onValueChange = onSessionNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.char_session_placeholder)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 角色名
                Text(
                    text = stringResource(R.string.char_name_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.char_name_placeholder)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 人设描述
                Text(
                    text = stringResource(R.string.char_persona_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.char_persona_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = persona,
                    onValueChange = { persona = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    placeholder = { Text(stringResource(R.string.char_persona_hint)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors(),
                    maxLines = 8
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 开场白
                Text(
                    text = stringResource(R.string.char_greeting_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.char_greeting_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = greeting,
                    onValueChange = { greeting = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    placeholder = { Text(stringResource(R.string.char_greeting_hint)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors(),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { showConfirm = true },
                colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.char_save))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    // 恢复默认
                    name = SettingsRepository.DEFAULT_CHARACTER_NAME
                    persona = SettingsRepository.DEFAULT_CHARACTER_PERSONA
                    greeting = SettingsRepository.DEFAULT_CHARACTER_GREETING
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.char_reset))
            }
        }
    )
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AccentPink,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    cursorColor = AccentPink
)
