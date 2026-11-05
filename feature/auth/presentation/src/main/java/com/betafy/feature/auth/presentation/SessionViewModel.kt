package com.betafy.feature.auth.presentation

import androidx.lifecycle.ViewModel
import com.betafy.feature.auth.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    fun isLoggedIn(): Boolean = repository.currentUserId != null
    fun currentUserId(): String? = repository.currentUserId
    fun signOut() = repository.signOut()
}
