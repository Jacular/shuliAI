package network

import app.cash.paging.Pager
import app.cash.paging.PagingData
import app.cash.paging.PagingConfig
import dao.PlatformSettings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import models.ChatMessage
import models.DeepSeekRequest
import models.DeepSeekResponse
import models.GeminiRequest
import models.GeminiResponse
import models.GptRequest
import models.GptResponse
import models.ModelConfig
import models.ModelProvider
import models.Products
import models.QwenRequest
import models.QwenResponse
import models.WenxinRequest
import models.WenxinResponse
import network.ApiService.getProducts
import utils.KeyType

class NetworkRepository(private val httpClient: HttpClient,private val platformSettings: PlatformSettings) {
    private val clients = mutableMapOf<ModelProvider, HttpClient>()

    init {
        // 初始化各模型客户端
        ModelProvider.values().forEach { modelType ->
            clients[modelType] = HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                    })
                }
            }
        }
    }

    fun getProducts(): Flow<PagingData<Products>> = Pager(
        config = PagingConfig(pageSize = 10, initialLoadSize = 10, enablePlaceholders = false,),
        pagingSourceFactory = {
            ResultPagingSource { page, _ ->
                delay(800)
                httpClient.getProducts(page).map { it.list }
            }
        }
    ).flow

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
        val response: GptResponse? = clients[ModelProvider.GPT]?.post("https://api.openai.com/v1/chat/completions") {
            header("Authorization", "Bearer ${getApiKey(ModelProvider.GPT)}")
            contentType(ContentType.Application.Json)
            setBody(
                GptRequest(
                model = "gpt-3.5-turbo",
                messages = messages.map { GptRequest.GptMessage(it.role.name.lowercase(), it.content) },
                temperature = config.temperature,
                max_tokens = config.maxTokens
            )
            )
        } as? GptResponse
        return response?.choices?.first()?.message?.content?:""
    }

    // ================== DeepSeek 实现 ==================
    private suspend fun handleDeepSeekRequest(
        messages: List<ChatMessage>,
        config: ModelConfig
    ): String {
        val response: DeepSeekResponse? = clients[ModelProvider.DEEPSEEK]?.post("https://api.deepseek.com/v1/chat/completions") {
            header("Authorization", "Bearer ${getApiKey(ModelProvider.DEEPSEEK)}")
            contentType(ContentType.Application.Json)
            setBody(
                DeepSeekRequest(
                messages = messages.map { DeepSeekRequest.DeepSeekMessage(it.role.name.lowercase(), it.content) },
                temperature = config.temperature,
                max_tokens = config.maxTokens
            )
            )
        } as? DeepSeekResponse
        return response?.choices?.first()?.message?.content?:""
    }

    // ================== 文心一言（ERNIE Bot）实现 ==================
    private suspend fun handleWenxinRequest(
        messages: List<ChatMessage>,
        config: ModelConfig
    ): String {
        // 第一步：获取Access Token
        val accessToken = getWenxinAccessToken()

        // 第二步：调用聊天接口
        val response: WenxinResponse = clients[ModelProvider.WENXIN]?.post(
            "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant"
        ) {
            parameter("access_token", accessToken)
            contentType(ContentType.Application.Json)
            setBody(
                WenxinRequest(
                messages = messages.map { WenxinRequest.WenxinMessage(it.role.name.lowercase(), it.content) },
                temperature = config.temperature,
                top_p = config.topP
            )
            )
        } as WenxinResponse
        return response.result
    }

    private suspend fun getWenxinAccessToken(): String {
        val response: JsonObject = httpClient.post(
            "https://aip.baidubce.com/oauth/2.0/token"
        ) {
            parameter("grant_type", "client_credentials")
            parameter("client_id", getApiKey(ModelProvider.WENXIN))
            parameter("client_secret", getApiKey(ModelProvider.WENXIN,))
        } as JsonObject
        return response["access_token"]?.jsonPrimitive?.content
            ?: throw Exception("Failed to get Wenxin access token")
    }

    // ================== 通义千问（Qwen）实现 ==================
    private suspend fun handleQwenRequest(
        messages: List<ChatMessage>,
        config: ModelConfig
    ): String {
        val response: QwenResponse = clients[ModelProvider.QWEN]?.post(
            "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation"
        ) {
            header("Authorization", "Bearer ${getApiKey(ModelProvider.QWEN)}")
            contentType(ContentType.Application.Json)
            setBody(QwenRequest(
                model = "qwen-turbo",
                input = QwenRequest.QwenInput(
                    messages = messages.map { QwenRequest.QwenMessage(it.role.name.lowercase(), it.content) }
                ),
                parameters = QwenRequest.QwenParameters(
                    temperature = config.temperature,
                    top_p = config.topP
                )
            ))
        } as QwenResponse
        return response.output.text
    }

    // ================== Gemini 实现 ==================
    private suspend fun handleGeminiRequest(
        messages: List<ChatMessage>,
        config: ModelConfig
    ): String {
        val response: GeminiResponse = clients[ModelProvider.GEMINI]?.post(
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"
        ) {
            parameter("key", getApiKey(ModelProvider.GEMINI))
            contentType(ContentType.Application.Json)
            setBody(GeminiRequest(
                contents = messages.map {
                    GeminiRequest.GeminiContent(listOf(GeminiRequest.GeminiPart(it.content)))
                },
                generationConfig = GeminiRequest.GeminiGenerationConfig(
                    temperature = config.temperature,
                    topP = config.topP,
                    maxOutputTokens = config.maxTokens
                )
            ))
        } as GeminiResponse
        return response.candidates.first().content.parts.first().text
    }


    // ================== 密钥管理 ==================
    private fun getApiKey(provider: ModelProvider): String {
        return when(provider) {
            ModelProvider.WENXIN -> platformSettings.getApiKey(provider, KeyType.CLIENT_ID)
            else -> platformSettings.getApiKey(provider, KeyType.API_KEY)
        }
    }
}