package com.shuli.cc.app.presentation

class ChatScreen {
    @Composable
    fun ChatScreen(sessionId: String) {
        val viewModel: ChatViewModel = koinViewModel()
        var inputText by remember { mutableStateOf("") }

        LaunchedEffect(sessionId) {
            viewModel.loadHistory(sessionId)
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
                items(viewModel.messages.value) { message ->
                    ChatMessageItem(message)
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
}