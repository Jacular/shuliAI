package com.shuli.cc.app.data.remote

// shared/commonMain/kotlin/data/remote/ApiClient.kt
class ApiClient {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        install(Logging) {
            level = LogLevel.HEADERS
        }
    }

    suspend fun sendChatRequest(
        provider: ModelProvider,
        messages: List<ChatMessage>,
        config: ModelConfig
    ): String {
        return when (provider) {
            ModelProvider.GPT -> handleGptRequest(messages, config)
            ModelProvider.DEEPSEEK -> handleDeepSeekRequest(messages, config)
            ModelProvider.WENXIN -> handleWenxinRequest(messages, config)
            ModelProvider.QWEN -> handleQwenRequest(messages, config)
            ModelProvider.GEMINI -> handleGeminiRequest(messages, config)
        }
    }

    // ================== GPT 实现 ==================
    private suspend fun handleGptRequest(
        messages: List<ChatMessage>,
        config: ModelConfig
    ): String {
        val response: GptResponse = httpClient.post("https://api.openai.com/v1/chat/completions") {
            header("Authorization", "Bearer ${getApiKey(ModelProvider.GPT)}")
            contentType(ContentType.Application.Json)
            setBody(GptRequest(
                model = "gpt-3.5-turbo",
                messages = messages.map { GptMessage(it.role.name.lowercase(), it.content) },
                temperature = config.temperature,
                max_tokens = config.maxTokens
            ))
        }
        return response.choices.first().message.content
    }

    // ================== DeepSeek 实现 ==================
    private suspend fun handleDeepSeekRequest(
        messages: List<ChatMessage>,
        config: ModelConfig
    ): String {
        val response: DeepSeekResponse = httpClient.post("https://api.deepseek.com/v1/chat/completions") {
            header("Authorization", "Bearer ${getApiKey(ModelProvider.DEEPSEEK)}")
            contentType(ContentType.Application.Json)
            setBody(DeepSeekRequest(
                messages = messages.map { DeepSeekMessage(it.role.name.lowercase(), it.content) },
                temperature = config.temperature,
                max_tokens = config.maxTokens
            ))
        }
        return response.choices.first().message.content
    }

    // ================== 文心一言（ERNIE Bot）实现 ==================
    private suspend fun handleWenxinRequest(
        messages: List<ChatMessage>,
        config: ModelConfig
    ): String {
        // 第一步：获取Access Token
        val accessToken = getWenxinAccessToken()

        // 第二步：调用聊天接口
        val response: WenxinResponse = httpClient.post(
            "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant"
        ) {
            parameter("access_token", accessToken)
            contentType(ContentType.Application.Json)
            setBody(WenxinRequest(
                messages = messages.map { WenxinMessage(it.role.name.lowercase(), it.content) },
                temperature = config.temperature,
                top_p = config.topP
            ))
        }
        return response.result
    }

    private suspend fun getWenxinAccessToken(): String {
        val response: JsonObject = httpClient.post(
            "https://aip.baidubce.com/oauth/2.0/token"
        ) {
            parameter("grant_type", "client_credentials")
            parameter("client_id", PlatformSettings.getApiKey(ModelProvider.WENXIN, KeyType.CLIENT_ID))
            parameter("client_secret", PlatformSettings.getApiKey(ModelProvider.WENXIN, KeyType.SECRET))
        }
        return response["access_token"]?.jsonPrimitive?.content
            ?: throw Exception("Failed to get Wenxin access token")
    }

    // ================== 通义千问（Qwen）实现 ==================
    private suspend fun handleQwenRequest(
        messages: List<ChatMessage>,
        config: ModelConfig
    ): String {
        val response: QwenResponse = httpClient.post(
            "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation"
        ) {
            header("Authorization", "Bearer ${getApiKey(ModelProvider.QWEN)}")
            contentType(ContentType.Application.Json)
            setBody(QwenRequest(
                model = "qwen-turbo",
                input = QwenInput(
                    messages = messages.map { QwenMessage(it.role.name.lowercase(), it.content) }
                ),
                parameters = QwenParameters(
                    temperature = config.temperature,
                    top_p = config.topP
                )
            ))
        }
        return response.output.text
    }

    // ================== Gemini 实现 ==================
    private suspend fun handleGeminiRequest(
        messages: List<ChatMessage>,
        config: ModelConfig
    ): String {
        val response: GeminiResponse = httpClient.post(
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"
        ) {
            parameter("key", getApiKey(ModelProvider.GEMINI))
            contentType(ContentType.Application.Json)
            setBody(GeminiRequest(
                contents = messages.map {
                    GeminiContent(listOf(GeminiPart(it.content)))
                },
                generationConfig = GeminiGenerationConfig(
                    temperature = config.temperature,
                    topP = config.topP,
                    maxOutputTokens = config.maxTokens
                )
            ))
        }
        return response.candidates.first().content.parts.first().text
    }

    // ================== 数据模型定义 ==================
    // GPT
    @Serializable
    private data class GptRequest(
        val model: String,
        val messages: List<GptMessage>,
        val temperature: Double,
        val max_tokens: Int
    ) {
        @Serializable
        data class GptMessage(val role: String, val content: String)
    }

    @Serializable
    private data class GptResponse(val choices: List<Choice>) {
        @Serializable
        data class Choice(val message: Message)
        @Serializable
        data class Message(val content: String)
    }

    // DeepSeek
    @Serializable
    private data class DeepSeekRequest(
        val model: String = "deepseek-chat",
        val messages: List<DeepSeekMessage>,
        val temperature: Double,
        val max_tokens: Int
    ) {
        @Serializable
        data class DeepSeekMessage(val role: String, val content: String)
    }

    @Serializable
    private data class DeepSeekResponse(val choices: List<Choice>) {
        @Serializable
        data class Choice(val message: Message)
        @Serializable
        data class Message(val content: String)
    }

    // 文心一言
    @Serializable
    private data class WenxinRequest(
        val messages: List<WenxinMessage>,
        val temperature: Double,
        val top_p: Double
    ) {
        @Serializable
        data class WenxinMessage(val role: String, val content: String)
    }

    @Serializable
    private data class WenxinResponse(val result: String)

    // 通义千问
    @Serializable
    private data class QwenRequest(
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
    private data class QwenResponse(val output: Output) {
        @Serializable
        data class Output(val text: String)
    }

    // Gemini
    @Serializable
    private data class GeminiRequest(
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
    private data class GeminiResponse(val candidates: List<Candidate>) {
        @Serializable
        data class Candidate(val content: Content)

        @Serializable
        data class Content(val parts: List<Part>)

        @Serializable
        data class Part(val text: String)
    }

    // ================== 密钥管理 ==================
    private fun getApiKey(provider: ModelProvider): String {
        return when(provider) {
            ModelProvider.WENXIN -> PlatformSettings.getApiKey(provider, KeyType.CLIENT_ID)
            else -> PlatformSettings.getApiKey(provider)
        }
    }
}

// 密钥类型扩展
enum class KeyType {
    API_KEY, CLIENT_ID, SECRET
}