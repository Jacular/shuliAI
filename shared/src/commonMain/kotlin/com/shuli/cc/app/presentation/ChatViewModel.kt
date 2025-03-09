package com.shuli.cc.app.presentation

import com.shuli.cc.app.data.repository.ChatRepository
import com.shuli.cc.app.domain.model.ChatMessage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatViewModel : KMMViewModel(), KoinComponent {
    private val repository: ChatRepository by inject()
    private val networkMonitor: NetworkMonitor by inject()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(sessionId: String, text: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.sendMessage(sessionId, text)
                    .catch { e ->
                        _messages.value += ChatMessage(
                            role = MessageRole.SYSTEM,
                            content = "Error: ${e.message}",
                            status = MessageStatus.ERROR
                        )
                    }
                    .collect { chunk ->
                        when(chunk) {
                            is InitialChunk -> {
                                _messages.value += ChatMessage(
                                    role = MessageRole.ASSISTANT,
                                    content = chunk.content,
                                    status = MessageStatus.SENDING
                                )
                            }
                            is StreamChunk -> {
                                _messages.update { messages ->
                                    messages.mapLastAssistant {
                                        it.copy(content = it.content + chunk.content)
                                    }
                                }
                            }
                            is FinalChunk -> {
                                _messages.update { messages ->
                                    messages.mapLastAssistant {
                                        it.copy(status = MessageStatus.SUCCESS)
                                    }
                                }
                            }
                        }
                    }
            _isLoading.value = false
        }
    }

    private fun List<ChatMessage>.mapLastAssistant(transform: (ChatMessage) -> ChatMessage): List<ChatMessage> {
        return mapIndexed { index, message ->
            if (index == lastIndex && message.role == MessageRole.ASSISTANT) {
                transform(message)
            } else {
                message
            }
        }
    }
}