package com.betafy.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betafy.feature.auth.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    fun isLoggedIn(): Boolean = repository.currentUserId != null
    fun currentUserId(): String? = repository.currentUserId

    fun refreshUsername() {
        viewModelScope.launch {
            _username.value = repository.currentUsername().orEmpty()
        }
    }

    fun signOut() {
        repository.signOut()
        _username.value = ""
    }
}
