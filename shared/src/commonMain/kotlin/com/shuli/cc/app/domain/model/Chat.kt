package com.shuli.cc.app.domain.model
import com.android.identity.util.UUID
import kotlinx.serialization.Serializable
@Serializable
data class ChatSession(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val modelConfig: ModelConfig = ModelConfig()
)

@Serializable
data class ModelConfig(
    val provider: ModelProvider = ModelProvider.GPT,
    val temperature: Double = 0.7,
    val maxTokens: Int = 2000,
    val topP: Double = 1.0
)

@Serializable
enum class ModelProvider {
    GPT, DEEPSEEK, WENXIN, QWEN, GEMINI
}

@Serializable
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sessionId: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENDING
)

@Serializable
enum class MessageRole { USER, ASSISTANT }

@Serializable
enum class MessageStatus { SENDING, SUCCESS, ERROR }