package com.betafy.feature.profile.data

import com.betafy.feature.profile.domain.model.FeedbackType
import com.betafy.feature.profile.domain.model.Genre
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class ProfileRemoteDataSource(
    private val database: FirebaseDatabase
) {
    suspend fun readProfile(node: String, userId: String): Map<String, Any?>? {
        val snapshot = withTimeout(TIMEOUT_MS) {
            database.reference.child(node).child(userId).get().await()
        }
        if (!snapshot.exists()) return null
        @Suppress("UNCHECKED_CAST")
        return snapshot.value as? Map<String, Any?>
    }

    suspend fun writeProfile(node: String, userId: String, values: Map<String, Any>) {
        withTimeout(TIMEOUT_MS) {
            database.reference.child(node).child(userId).setValue(values).await()
        }
    }

    suspend fun readGenres(): List<Genre> = readCatalog(GENRES).map { (id, name) -> Genre(id, name) }

    suspend fun readFeedbackTypes(): List<FeedbackType> =
        readCatalog(FEEDBACK_TYPES).map { (id, name) -> FeedbackType(id, name) }

    private suspend fun readCatalog(node: String): List<Pair<String, String>> {
        val snapshot = withTimeout(TIMEOUT_MS) {
            database.reference.child(node).get().await()
        }
        if (!snapshot.exists()) return emptyList()
        return snapshot.children.mapNotNull { child ->
            val id = child.key ?: return@mapNotNull null
            val name = when (val value = child.value) {
                is String -> value
                is Map<*, *> -> value["name"]?.toString()
                else -> null
            }?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            id to name
        }
    }

    private companion object {
        const val TIMEOUT_MS = 15_000L
        const val GENRES = "genres"
        const val FEEDBACK_TYPES = "feedback_types"
    }
}
