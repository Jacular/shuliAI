package com.shuli.cc.app.data.repository

import com.shuli.cc.app.data.local.ChatDao
import com.shuli.cc.app.data.remote.ApiClient
import com.shuli.cc.app.domain.model.ChatMessage
import com.shuli.cc.app.domain.model.MessageRole
import com.shuli.cc.app.domain.model.MessageStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// shared/src/commonMain/kotlin/data/repository/ChatRepositoryImpl.kt
class ChatRepositoryImpl(
    private val chatDao: ChatDao,
    private val apiClient: ApiClient,
    private val networkMonitor: NetworkMonitor
) : ChatRepository {

    override suspend fun sendMessage(sessionId: String, content: String): Flow<MessageChunk> = flow {
        // 保存用户消息
        val userMessage = ChatMessage(
            sessionId = sessionId,
            role = MessageRole.USER,
            content = content,
            status = MessageStatus.SENDING
        )
        chatDao.insertMessage(userMessage.toEntity())

        // 获取当前会话配置
        val config = chatDao.getSession(sessionId)?.toModelConfig()
            ?: throw IllegalStateException("Session not found")

        // 获取历史消息
        val messages = chatDao.getMessagesBySession(sessionId)
                .map { it.toDomain() }

        // 处理流式响应
        apiClient.streamChatCompletion(
            provider = config.provider,
            messages = messages,
            config = config
        ).collect { chunk ->
            when(chunk) {
                is InitialChunk -> {
                    chatDao.insertMessage(
                        ChatMessage(
                            sessionId = sessionId,
                            role = MessageRole.ASSISTANT,
                            content = chunk.content,
                            model = config.provider,
                            status = MessageStatus.SENDING
                        ).toEntity()
                    )
                }
                is StreamChunk -> {
                    chatDao.updateMessageContent(
                        sessionId = sessionId,
                        newContent = chunk.content
                    )
                }
                is FinalChunk -> {
                    chatDao.updateMessageStatus(
                        sessionId = sessionId,
                        status = MessageStatus.SUCCESS
                    )
                }
                is ErrorChunk -> {
                    chatDao.updateMessageStatus(
                        sessionId = sessionId,
                        status = MessageStatus.ERROR,
                        error = chunk.message
                    )
                }
            }
            emit(chunk)
        }
    }
}