package com.betafy.feature.notification.data

import android.util.Log
import com.betafy.feature.notification.domain.model.InboxNotification
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

class NotificationRemoteDataSource(
    private val database: FirebaseDatabase
) {
    fun observe(userId: String): Flow<List<InboxNotification>> {
        if (userId.isBlank()) return flowOf(emptyList())
        return callbackFlow {
            val query = database.reference.child(NODE).orderByChild(USER_ID).equalTo(userId)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val rows = snapshot.children.mapNotNull { child ->
                        val id = child.key ?: return@mapNotNull null
                        val value = child.value as? Map<*, *> ?: return@mapNotNull null
                        id to value
                    }
                    trySend(NotificationRecords.parse(userId, rows))
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "чтение: ${error.message}")
                    close(error.toException())
                }
            }
            Log.i(TAG, "подписка: $userId")
            query.addValueEventListener(listener)
            awaitClose { query.removeEventListener(listener) }
        }
    }

    suspend fun list(userId: String): List<InboxNotification> {
        if (userId.isBlank()) return emptyList()
        val snapshot = withTimeout(TIMEOUT_MS) {
            database.reference.child(NODE).orderByChild(USER_ID).equalTo(userId).get().await()
        }
        val rows = snapshot.children.mapNotNull { child ->
            val id = child.key ?: return@mapNotNull null
            val value = child.value as? Map<*, *> ?: return@mapNotNull null
            id to value
        }
        return NotificationRecords.parse(userId, rows)
    }

    suspend fun save(userId: String, type: String, text: String, relatedEntityId: String?) {
        val ref = database.reference.child(NODE).push()
        val id = ref.key ?: error("Не удалось создать уведомление")
        Log.i(TAG, "запись: $id тип $type")
        withTimeout(TIMEOUT_MS) {
            ref.setValue(NotificationRecords.values(userId, type, text, relatedEntityId)).await()
        }
    }

    suspend fun markRead(ids: List<String>) {
        val updates = mutableMapOf<String, Any?>()
        ids.forEach { updates["$NODE/$it/is_read"] = true }
        if (updates.isEmpty()) return
        Log.i(TAG, "прочитано: ${updates.size}")
        withTimeout(TIMEOUT_MS) {
            database.reference.updateChildren(updates).await()
        }
    }

    suspend fun delete(ids: List<String>) {
        val updates = mutableMapOf<String, Any?>()
        ids.forEach { updates["$NODE/$it"] = null }
        if (updates.isEmpty()) return
        Log.i(TAG, "удалено: ${updates.size}")
        withTimeout(TIMEOUT_MS) {
            database.reference.updateChildren(updates).await()
        }
    }

    private companion object {
        const val TAG = "BetafyNotification"
        const val NODE = "notifications"
        const val USER_ID = "user_id"
        const val TIMEOUT_MS = 15_000L
    }
}
