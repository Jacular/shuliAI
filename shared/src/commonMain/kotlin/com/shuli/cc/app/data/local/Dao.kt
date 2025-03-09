package com.shuli.cc.app.data.local

import com.shuli.cc.app.domain.model.ChatMessage
import com.shuli.cc.app.database.ChatQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ChatDao(
    private val queries: ChatQueries
) {
    // region 基础CRUD操作
    suspend fun insertMessage(message: ChatMessage) {
        withContext(Dispatchers.IO) {
            queries.createMessage(
                id = message.id,
                session_id = message.sessionId,
                role = message.role.name,
                content = message.content,
                model = message.model.name,
                timestamp = message.timestamp,
                status = message.status.name,
                error = message.error
            )
        }
    }

    suspend fun getMessagesBySession(sessionId: String): List<ChatMessage> {
        return withContext(Dispatchers.IO) {
            queries.getMessagesBySession(sessionId)
                    .executeAsList()
                    .map { it.toDomain() }
        }
    }

    suspend fun updateMessageContent(messageId: String, newContent: String) {
        withContext(Dispatchers.IO) {
            queries.updateMessageContent(newContent, messageId)
        }
    }

    suspend fun updateMessageStatus(
        messageId: String,
        status: MessageStatus,
        error: String? = null
    ) {
        withContext(Dispatchers.IO) {
            queries.updateMessageStatus(status.name, error, messageId)
        }
    }

    suspend fun deleteMessage(messageId: String) {
        withContext(Dispatchers.IO) {
            queries.deleteMessage(messageId)
        }
    }
    // endregion

    // region 高级查询
    suspend fun observeMessages(sessionId: String): Flow<List<ChatMessage>> {
        return callbackFlow {
            val listener = object : Query.Listener {
                override fun queryResultsChanged() {
                    trySend(getMessagesBySession(sessionId))
                }
            }

            queries.getMessagesBySession(sessionId).addListener(listener)
            awaitClose {
                queries.getMessagesBySession(sessionId).removeListener(listener)
            }
        }.flowOn(Dispatchers.IO)
    }
    // endregion
}