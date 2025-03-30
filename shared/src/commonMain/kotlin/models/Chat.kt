package models
import kotlinx.serialization.Serializable
@Serializable
data class ChatSession(
    val id: String = "UUID.randomUUID().toString()",
    val title: String,
    val createdAt: Long = 11111,
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
    val id: String =" UUID.randomUUID().toString()",
    val sessionId: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = 11111,
    val status: MessageStatus = MessageStatus.SENDING
)

@Serializable
enum class MessageRole { USER, ASSISTANT,SYSTEM }

@Serializable
enum class MessageStatus { SENDING, SUCCESS, ERROR }

// ================== 数据模型定义 ==================
// GPT
@Serializable
data class GptRequest(
    val model: String,
    val messages: List<GptMessage>,
    val temperature: Double,
    val max_tokens: Int
) {
    @Serializable
    data class GptMessage(val role: String, val content: String)
}

@Serializable
data class GptResponse(val choices: List<Choice>) {
    @Serializable
    data class Choice(val message: Message)
    @Serializable
    data class Message(val content: String)
}

// DeepSeek
@Serializable
data class DeepSeekRequest(
    val model: String = "deepseek-chat",
    val messages: List<DeepSeekMessage>,
    val temperature: Double,
    val max_tokens: Int
) {
    @Serializable
    data class DeepSeekMessage(val role: String, val content: String)
}

@Serializable
data class DeepSeekResponse(val choices: List<Choice>) {
    @Serializable
    data class Choice(val message: Message)
    @Serializable
    data class Message(val content: String)
}

// 文心一言
@Serializable
data class WenxinRequest(
    val messages: List<WenxinMessage>,
    val temperature: Double,
    val top_p: Double
) {
    @Serializable
    data class WenxinMessage(val role: String, val content: String)
}

@Serializable
data class WenxinResponse(val result: String)

// 通义千问
@Serializable
data class QwenRequest(
    val model: String,
    val input: QwenInput,
    val parameters: QwenParameters
) {
    @Serializable
    data class QwenInput(val messages: List<QwenMessage>)

    @Serializable
    data class QwenMessage(val role: String, val content: String)

    @Serializable
    data class QwenParameters(
        val temperature: Double,
        val top_p: Double
    )
}

@Serializable
data class QwenResponse(val output: Output) {
    @Serializable
    data class Output(val text: String)
}

// Gemini
@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig
) {
    @Serializable
    data class GeminiContent(val parts: List<GeminiPart>)

    @Serializable
    data class GeminiPart(val text: String)

    @Serializable
    data class GeminiGenerationConfig(
        val temperature: Double,
        val topP: Double,
        val maxOutputTokens: Int
    )
}

@Serializable
data class GeminiResponse(val candidates: List<Candidate>) {
    @Serializable
    data class Candidate(val content: Content)

    @Serializable
    data class Content(val parts: List<Part>)

    @Serializable
    data class Part(val text: String)
}