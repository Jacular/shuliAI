package dao

import models.ChatMessage

interface LLMProvider {
    suspend fun chatCompletion(messages: List<ChatMessage>): String
    val modelName: String
}