package com.betafy.feature.auth.domain

interface AuthRepository {
    val currentUserId: String?
    suspend fun signUp(
        email: String,
        password: String,
        username: String
    ): Result<String>
    suspend fun signIn(email: String, password: String): Result<String>
    fun signOut()
}
