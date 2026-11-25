package com.betafy.feature.chats.data

import android.util.Log
import com.betafy.feature.chats.domain.model.ChatMessage
import com.betafy.feature.chats.domain.model.ChatSummary
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class ChatRemoteDataSource(
    private val database: FirebaseDatabase
) {
    fun observeChats(userId: String): Flow<List<ChatSummary>> {
        if (userId.isBlank()) return flowOf(emptyList())
        return callbackFlow {
            val chats = emptyMap<String, Map<*, *>>()
            var matches = emptyMap<String, Map<*, *>>()
            var users = emptyMap<String, Map<*, *>>()
            fun emitNow() {
                trySend(ChatRecords.summaries(userId, chats, matches, users))
            }
            val matchesListener = nodeListener({ matches = it; emitNow() }) { close(it.toException()) }
            val usersListener = nodeListener({ users = it; emitNow() }) {
                users = emptyMap()
                emitNow()
            }
            val root = database.reference
            Log.i(TAG, "подписка: $userId")
            root.child(MATCHES).addValueEventListener(matchesListener)
            root.child(USERS).addValueEventListener(usersListener)
            awaitClose {
                root.child(MATCHES).removeEventListener(matchesListener)
                root.child(USERS).removeEventListener(usersListener)
            }
        }
    }

    fun observeMessages(chatId: String, userId: String): Flow<List<ChatMessage>> {
        if (chatId.isBlank()) return flowOf(emptyList())
        return callbackFlow {
            val query = messages(chatId)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val rows = snapshot.children.mapNotNull { child ->
                        val id = child.key ?: return@mapNotNull null
                        val value = child.value as? Map<*, *> ?: return@mapNotNull null
                        id to value
                    }
                    trySend(ChatRecords.messages(userId, chatId, rows))
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "сообщения: ${error.message}")
                    close(error.toException())
                }
            }
            query.addValueEventListener(listener)
            awaitClose { query.removeEventListener(listener) }
        }
    }

    suspend fun ensureChat(chatId: String, matchId: String) {
        if (chatId.isBlank() || matchId.isBlank()) error("Не удалось открыть чат")
    }

    suspend fun send(chatId: String, senderId: String, text: String) {
        val ref = messages(chatId).push()
        val id = ref.key ?: error("Не удалось отправить сообщение")
        Log.i(TAG, "сообщение: $id чат $chatId")
        withTimeout(TIMEOUT_MS) {
            ref.setValue(ChatRecords.messageValues(chatId, senderId, text, System.currentTimeMillis())).await()
        }
    }

    suspend fun markRead(chatId: String, messageIds: List<String>) {
        val updates = mutableMapOf<String, Any?>()
        messageIds.forEach { id ->
            updates["$MATCHES/$chatId/$MESSAGES/$id/isRead"] = true
        }
        if (updates.isEmpty()) return
        Log.i(TAG, "прочитано: ${messageIds.size}")
        withTimeout(TIMEOUT_MS) {
            database.reference.updateChildren(updates).await()
        }
    }

    private fun nodeListener(
        onValue: (Map<String, Map<*, *>>) -> Unit,
        onCancelled: (DatabaseError) -> Unit
    ): ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            onValue(rows(snapshot))
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w(TAG, error.message)
            onCancelled(error)
        }
    }

    private fun rows(snapshot: DataSnapshot): Map<String, Map<*, *>> =
        snapshot.children.mapNotNull { child ->
            val id = child.key ?: return@mapNotNull null
            val value = child.value as? Map<*, *> ?: return@mapNotNull null
            id to value
        }.toMap()

    private fun messages(chatId: String) =
        database.reference.child(MATCHES).child(chatId).child(MESSAGES)

    private companion object {
        const val TAG = "BetafyChat"
        const val TIMEOUT_MS = 15_000L
        const val MESSAGES = "messages"
        const val MATCHES = "matches"
        const val USERS = "Users"
    }
}
