package com.shuli.cc.app.presentation

import androidx.compose.runtime.Composable

// shared/commonMain/kotlin/presentation/ChatScreen.kt
@Composable
fun ChatScreen(viewModel: ChatViewModel = koinViewModel()) {
    val messages by viewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 消息列表
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages) { message ->
                ChatMessageItem(message)
            }
        }

        // 输入框
        InputField(
            text = inputText,
            onTextChange = { inputText = it },
            onSend = {
                viewModel.sendMessage(inputText)
                inputText = ""
            },
            isLoading = viewModel.isLoading.value
        )
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val backgroundColor = when(message.role) {
        MessageRole.USER -> Color.Blue.copy(alpha = 0.1f)
        MessageRole.ASSISTANT -> Color.LightGray.copy(alpha = 0.1f)
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        modifier = Modifier.padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = message.content)
            if (message.status == MessageStatus.SENDING) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            }
        }
    }
}