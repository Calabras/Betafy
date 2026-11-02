package com.betafy.feature.auth.data

import com.betafy.feature.auth.domain.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : AuthRepository {

    override val currentUserId: String?
        get() = auth.currentUser?.uid

    override suspend fun signUp(
        email: String,
        password: String,
        username: String
    ): Result<String> = runCatching {
        val firebaseUser = try {
            auth.createUserWithEmailAndPassword(email, password).await().user
        } catch (e: FirebaseAuthUserCollisionException) {
            throw IllegalStateException("Эта почта уже зарегистрирована")
        } ?: error("User id is missing")

        val uid = firebaseUser.uid
        val nickKey = username.lowercase()
        val root = database.reference

        try {
            withTimeout(15_000) {
                val taken = root.child("usernames").child(nickKey).get().await().exists()
                if (taken) throw IllegalStateException("Этот ник уже занят")

                val user = mapOf(
                    "email" to email,
                    "username" to username,
                    "avatarUrl" to "",
                    "createdAt" to ServerValue.TIMESTAMP
                )
                root.updateChildren(
                    mapOf(
                        "Users/$uid" to user,
                        "usernames/$nickKey" to uid
                    )
                ).await()
            }
        } catch (e: Exception) {
            runCatching { withTimeout(5_000) { firebaseUser.delete().await() } }
            auth.signOut()
            if (e is TimeoutCancellationException) {
                throw IllegalStateException("Нет связи с базой. Проверьте интернет или включите VPN")
            }
            throw e
        }

        auth.signOut()
        uid
    }

    override suspend fun signIn(email: String, password: String): Result<String> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await().user?.uid
            ?: error("User id is missing")
    }

    override fun signOut() = auth.signOut()
}
