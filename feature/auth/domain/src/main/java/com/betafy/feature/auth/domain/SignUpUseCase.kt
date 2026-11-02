package com.betafy.feature.auth.domain

class SignUpUseCase(private val repository: AuthRepository) {

    private val nickRegex = Regex("^[\\p{L}\\p{N}_-]{3,20}$")

    suspend operator fun invoke(
        username: String,
        email: String,
        password: String
    ): Result<String> {
        if (!nickRegex.matches(username.trim())) {
            return Result.failure(IllegalArgumentException("Ник: 3–20 символов, буквы, цифры, _ и -"))
        }
        if (!email.contains("@") || email.length < 5) return Result.failure(IllegalArgumentException("Некорректная почта"))
        if (password.length < 6) return Result.failure(IllegalArgumentException("Пароль должен быть не короче 6 символов"))
        return repository.signUp(email.trim(), password, username.trim())
    }
}
