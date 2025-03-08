package com.shuli.cc.app.presentation

class ChatViewModel(
    private val repository: ChatRepository,
    private val contextManager: ContextManager
) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(sessionId: String, text: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.sendMessage(sessionId, text).collect { chunk ->
                    _messages.update { messages ->
                        messages.updateLastAssistant { msg ->
                            msg.copy(content = msg.content + chunk)
                        }
                    }
                }
            } catch (e: Exception) {
                _messages.update { messages +
                        ChatMessage(
                            role = MessageRole.SYSTEM,
                            content = "错误: ${e.message}",
                            status = MessageStatus.ERROR
                        )
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadHistory(sessionId: String) {
        viewModelScope.launch {
            repository.getMessages(sessionId).collect { messages ->
                _messages.value = messages
            }
        }
    }
}