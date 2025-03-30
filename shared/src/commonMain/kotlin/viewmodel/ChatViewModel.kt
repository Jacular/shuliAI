package viewmodel

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import models.ChatMessage
import models.MessageRole
import models.MessageStatus
import models.ModelConfig
import models.ModelProvider
import models.Products
import network.ApiService.getProducts
import network.NetworkRepository
import network.ResultPagingSource
import network.map

class ChatViewModel(private val repository: NetworkRepository) {


    private val _messages = MutableStateFlow("")
    val messages: StateFlow<String> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(sessionId: String, text: String){
        flow<String> {
            val repository=repository.sendChatRequest(provider = ModelProvider.WENXIN,
                messages = listOf(ChatMessage("nihao","", MessageRole.USER,"",111, MessageStatus.SENDING)),
                config = ModelConfig(temperature = 0.8))
            emit(repository)
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