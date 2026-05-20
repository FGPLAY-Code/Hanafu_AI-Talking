package com.hanafu.app.ui.agreement

import com.hanafu.app.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.hanafu.app.ui.theme.AccentPink

/**
 * 用户协议页面
 * 首次启动时展示，同意后永久不再显示
 */
@Composable
fun UserAgreementScreen(
    onAgreed: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // 标题
        Text(
            text = stringResource(R.string.agreement_title),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.agreement_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 协议内容（可滚动）
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            AgreementSection(
                title = stringResource(R.string.agreement_section1_title),
                content = stringResource(R.string.agreement_section1_body)
            )

            AgreementSection(
                title = stringResource(R.string.agreement_section2_title),
                content = stringResource(R.string.agreement_section2_body)
            )

            AgreementSection(
                title = stringResource(R.string.agreement_section3_title),
                content = stringResource(R.string.agreement_section3_body)
            )

            AgreementSection(
                title = stringResource(R.string.agreement_section4_title),
                content = stringResource(R.string.agreement_section4_body)
            )

            AgreementSection(
                title = stringResource(R.string.agreement_section5_title),
                content = stringResource(R.string.agreement_section5_body)
            )

            AgreementSection(
                title = stringResource(R.string.agreement_section6_title),
                content = stringResource(R.string.agreement_section6_body)
            )

            AgreementSection(
                title = stringResource(R.string.agreement_section7_title),
                content = stringResource(R.string.agreement_section7_body)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 署名
            Text(
                text = stringResource(R.string.agreement_signature),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )

            Text(
                text = stringResource(R.string.agreement_year),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 同意按钮
        Button(
            onClick = onAgreed,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentPink
            )
        ) {
            Text(
                text = stringResource(R.string.agreement_button),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AgreementSection(
    title: String,
    content: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
        )
    }
}
