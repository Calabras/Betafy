package com.betafy.feature.match.data

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class MatchRemoteDataSource(
    private val database: FirebaseDatabase
) {
    suspend fun read(node: String): Map<String, Any?> {
        Log.i(TAG, "$node чтение: старт")
        try {
            val snapshot = withTimeout(TIMEOUT_MS) {
                database.reference.child(node).get().await()
            }
            if (!snapshot.exists()) {
                Log.i(TAG, "$node чтение: пусто")
                return emptyMap()
            }
            val map = snapshot.value.asNodeMap()
            Log.i(TAG, "$node чтение: ${map.size}")
            return map
        } catch (error: Exception) {
            Log.i(TAG, "$node чтение: ошибка ${error.javaClass.simpleName}: ${error.message}")
            throw error
        }
    }

    suspend fun createMatch(values: Map<String, Any>) {
        val ref = database.reference.child(MATCHES).push()
        Log.i(TAG, "matches/${ref.key} запись: старт")
        try {
            withTimeout(TIMEOUT_MS) {
                ref.setValue(values).await()
            }
            Log.i(TAG, "matches/${ref.key} запись: сохранено, полей ${values.size}")
        } catch (error: Exception) {
            Log.i(TAG, "matches/${ref.key} запись: ошибка ${error.javaClass.simpleName}: ${error.message}")
            throw error
        }
    }

    private companion object {
        const val TAG = "BetafyMatch"
        const val TIMEOUT_MS = 15_000L
        const val MATCHES = "matches"
    }
}
