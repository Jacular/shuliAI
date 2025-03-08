package com.shuli.cc.app.data.repository

import com.shuli.cc.app.domain.model.ChatMessage
import com.shuli.cc.app.domain.model.ChatSession
import com.shuli.cc.app.domain.model.ModelConfig
import kotlinx.coroutines.flow.Flow

// 仓储层接口
interface ChatRepository {
    // 会话管理
    suspend fun createNewSession(modelConfig: ModelConfig): ChatSession
    suspend fun deleteSession(sessionId: String)
    fun observeAllSessions(): Flow<List<ChatSession>>

    // 消息管理
    suspend fun sendMessage(sessionId: String, content: String): Flow<MessageChunk>
    fun observeMessages(sessionId: String): Flow<List<ChatMessage>>
    suspend fun retryMessage(messageId: String)
    suspend fun deleteMessage(messageId: String)

    // 配置管理
    suspend fun updateModelConfig(sessionId: String, config: ModelConfig)
    suspend fun getModelConfig(sessionId: String): ModelConfig
}

interface MessageChunk {
    val sessionId: String
    val messageId: String
    val content: String
    val isFinal: Boolean
}