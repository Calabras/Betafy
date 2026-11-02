package com.betafy.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betafy.feature.auth.domain.SignInUseCase
import com.betafy.feature.auth.domain.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun onFullNameChange(value: String) = _state.update { it.copy(fullName = value, error = null) }
    fun onUsernameChange(value: String) = _state.update { it.copy(username = value, error = null) }
    fun onEmailChange(value: String) = _state.update { it.copy(email = value, error = null) }
    fun onPasswordChange(value: String) = _state.update { it.copy(password = value, error = null) }

    fun signIn() {
        val s = _state.value
        if (s.isLoading) return
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            signInUseCase(s.email, s.password)
                .onSuccess { _state.update { it.copy(isLoading = false, isSuccess = true) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Ошибка входа") } }
        }
    }

    fun signUp() {
        val s = _state.value
        if (s.isLoading) return
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            signUpUseCase(s.username, s.email, s.password)
                .onSuccess { _state.update { it.copy(isLoading = false, isRegistered = true) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Ошибка регистрации") } }
        }
    }
}
