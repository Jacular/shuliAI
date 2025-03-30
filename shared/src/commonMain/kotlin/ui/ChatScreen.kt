package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import models.ChatMessage
import models.MessageRole
import models.MessageStatus
import org.koin.compose.getKoin
import viewmodel.ChatViewModel

// shared/commonMain/kotlin/ChatScreen.kt
@Composable
fun ChatScreen(sessionId: String) {
    val viewModel: ChatViewModel = getKoin().get()
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(sessionId) {
        viewModel.sendMessage(sessionId,"")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 消息列表
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(viewModel.messages.value.length) { message ->
                ChatMessageItem(ChatMessage("nihao","", MessageRole.USER,"哈哈哈哈哈",111, MessageStatus.SENDING))
            }
        }

        // 输入框
        InputField(
            text = inputText,
            onTextChange = { inputText = it },
            onSend = {
                viewModel.sendMessage(sessionId, inputText)
                inputText = ""
            },
            isLoading = viewModel.isLoading.value
        )
    }
}

@Composable
private fun ChatMessageItem(message: ChatMessage) {
    val bubbleColor = when (message.role) {
        MessageRole.USER -> Color.Blue.copy(alpha = 0.1f)
        MessageRole.ASSISTANT -> Color.LightGray.copy(alpha = 0.1f)
        MessageRole.SYSTEM -> Color.Red.copy(alpha = 0.1f)
    }

    Box(
        modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(bubbleColor, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = message.content,
                modifier = Modifier.fillMaxWidth()
            )

            if (message.status == MessageStatus.SENDING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun InputField(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("输入消息...") }
        )

        Button(
            onClick = onSend,
            enabled = text.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("发送")
            }
        }
    }
}