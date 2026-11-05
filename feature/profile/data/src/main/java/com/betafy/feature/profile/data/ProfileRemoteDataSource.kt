package com.betafy.feature.profile.data

import com.betafy.feature.profile.domain.ProfileTrace
import com.betafy.feature.profile.domain.model.FeedbackType
import com.betafy.feature.profile.domain.model.Genre
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class ProfileRemoteDataSource(
    private val database: FirebaseDatabase,
    private val trace: ProfileTrace
) {
    suspend fun readProfile(node: String, userId: String): Map<String, Any?>? = traced(node, userId, "чтение") {
        val snapshot = withTimeout(TIMEOUT_MS) {
            database.reference.child(node).child(userId).get().await()
        }
        if (!snapshot.exists()) {
            trace.event("$node/$userId чтение: записи нет")
            return@traced null
        }
        trace.event("$node/$userId чтение: запись есть")
        @Suppress("UNCHECKED_CAST")
        snapshot.value as? Map<String, Any?>
    }

    suspend fun writeProfile(node: String, userId: String, values: Map<String, Any>) {
        traced(node, userId, "запись") {
            withTimeout(TIMEOUT_MS) {
                database.reference.child(node).child(userId).setValue(values).await()
            }
            trace.event("$node/$userId запись: сохранено, полей ${values.size}")
        }
    }

    suspend fun readGenres(): List<Genre> = readCatalog(GENRES).map { (id, name) -> Genre(id, name) }

    suspend fun readFeedbackTypes(): List<FeedbackType> =
        readCatalog(FEEDBACK_TYPES).map { (id, name) -> FeedbackType(id, name) }

    private suspend fun readCatalog(node: String): List<Pair<String, String>> {
        trace.event("$node справочник: старт")
        return try {
            val snapshot = withTimeout(TIMEOUT_MS) {
                database.reference.child(node).get().await()
            }
            if (!snapshot.exists()) {
                trace.event("$node справочник: пусто")
                return emptyList()
            }
            val items = snapshot.children.mapNotNull { child ->
                val id = child.key ?: return@mapNotNull null
                val name = when (val value = child.value) {
                    is String -> value
                    is Map<*, *> -> value["name"]?.toString()
                    else -> null
                }?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                id to name
            }
            trace.event("$node справочник: ${items.size}")
            items
        } catch (error: Exception) {
            trace.event("$node справочник: ошибка ${error.javaClass.simpleName}: ${error.message}")
            throw error
        }
    }

    private suspend fun <T> traced(node: String, userId: String, action: String, block: suspend () -> T): T {
        trace.event("$node/$userId $action: старт")
        return try {
            block()
        } catch (error: Exception) {
            trace.event("$node/$userId $action: ошибка ${error.javaClass.simpleName}: ${error.message}")
            throw error
        }
    }

    private companion object {
        const val TIMEOUT_MS = 15_000L
        const val GENRES = "genres"
        const val FEEDBACK_TYPES = "feedback_types"
    }
}
