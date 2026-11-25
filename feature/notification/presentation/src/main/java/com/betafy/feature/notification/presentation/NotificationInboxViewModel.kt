package com.betafy.feature.notification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betafy.feature.notification.domain.DeleteNotificationsUseCase
import com.betafy.feature.notification.domain.EnsureNotificationUseCase
import com.betafy.feature.notification.domain.MarkNotificationsReadUseCase
import com.betafy.feature.notification.domain.ObserveNotificationsUseCase
import com.betafy.feature.notification.domain.SaveNotificationUseCase
import com.betafy.feature.notification.domain.SignalNotificationUseCase
import com.betafy.feature.notification.domain.model.InboxNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object NotificationKinds {
    const val PROFILE = "profile"
    const val INTEREST = "interest"
    const val MESSAGE = "message"
}

data class NotificationUiState(
    val loading: Boolean = true,
    val items: List<InboxNotification> = emptyList(),
    val error: String? = null
) {
    val unread: Int get() = items.count { !it.isRead }
}

@HiltViewModel
class NotificationInboxViewModel @Inject constructor(
    private val observeNotifications: ObserveNotificationsUseCase,
    private val ensureNotification: EnsureNotificationUseCase,
    private val signalNotification: SignalNotificationUseCase,
    private val saveNotification: SaveNotificationUseCase,
    private val markNotificationsRead: MarkNotificationsReadUseCase,
    private val deleteNotifications: DeleteNotificationsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationUiState())
    val state: StateFlow<NotificationUiState> = _state.asStateFlow()

    private var openedFor: String? = null
    private var listen: Job? = null
    private val ensuring = mutableSetOf<String>()
    private val signalGate = Mutex()

    fun open(userId: String) {
        if (userId.isBlank()) {
            listen?.cancel()
            openedFor = null
            _state.value = NotificationUiState(loading = false)
            return
        }
        if (userId == openedFor && listen?.isActive == true) return
        openedFor = userId
        listen?.cancel()
        listen = viewModelScope.launch {
            observeNotifications(userId)
                .catch { error ->
                    _state.update {
                        it.copy(loading = false, error = error.message ?: "Не удалось загрузить уведомления")
                    }
                }
                .collect { items ->
                    _state.update { it.copy(loading = false, items = items, error = null) }
                }
        }
    }

    fun ensure(userId: String, type: String, text: String, relatedEntityId: String) {
        if (userId.isBlank() || text.isBlank() || relatedEntityId.isBlank()) return
        if (!ensuring.add(relatedEntityId)) return
        viewModelScope.launch {
            ensureNotification(userId, type, text, relatedEntityId).onFailure { error ->
                ensuring.remove(relatedEntityId)
                _state.update { it.copy(error = error.message) }
            }
        }
    }

    fun signal(userId: String, type: String, text: String, relatedEntityId: String) {
        if (userId.isBlank() || text.isBlank() || relatedEntityId.isBlank()) return
        viewModelScope.launch {
            signalGate.withLock {
                signalNotification(userId, type, text, relatedEntityId).onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
            }
        }
    }

    fun record(userId: String, type: String, text: String, relatedEntityId: String? = null) {
        if (userId.isBlank() || text.isBlank()) return
        viewModelScope.launch {
            saveNotification(userId, type, text, relatedEntityId).onFailure { error ->
                _state.update { it.copy(error = error.message) }
            }
        }
    }

    fun markRead(ids: List<String>) {
        if (ids.isEmpty()) return
        viewModelScope.launch {
            markNotificationsRead(ids).onFailure { error ->
                _state.update { it.copy(error = error.message) }
            }
        }
    }

    fun delete(ids: List<String>) {
        if (ids.isEmpty()) return
        viewModelScope.launch {
            deleteNotifications(ids).onFailure { error ->
                _state.update { it.copy(error = error.message) }
            }
        }
    }
}
