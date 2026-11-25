package com.betafy.feature.chats.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betafy.feature.chats.domain.MarkMessagesReadUseCase
import com.betafy.feature.chats.domain.ObserveChatsUseCase
import com.betafy.feature.chats.domain.ObserveMessagesUseCase
import com.betafy.feature.chats.domain.OpenChatUseCase
import com.betafy.feature.chats.domain.SendMessageUseCase
import com.betafy.feature.chats.domain.model.ChatMessage
import com.betafy.feature.chats.domain.model.ChatSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class IncomingChat(
    val matchId: String,
    val messageId: String,
    val peerName: String,
    val peerUsername: String
)

data class OpenedChat(
    val summary: ChatSummary,
    val messages: List<ChatMessage> = emptyList(),
    val draft: String = "",
    val sending: Boolean = false
)

data class ChatsUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val chats: List<ChatSummary> = emptyList(),
    val opened: OpenedChat? = null
) {
    val unread: Int get() = chats.sumOf { it.unread }
}

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val observeChats: ObserveChatsUseCase,
    private val observeMessages: ObserveMessagesUseCase,
    private val openChat: OpenChatUseCase,
    private val sendMessage: SendMessageUseCase,
    private val markRead: MarkMessagesReadUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ChatsUiState())
    val state: StateFlow<ChatsUiState> = _state.asStateFlow()
    private val _incoming = MutableSharedFlow<IncomingChat>(extraBufferCapacity = 8)
    val incoming: SharedFlow<IncomingChat> = _incoming.asSharedFlow()
    private val signaled = mutableSetOf<String>()
    private var userId: String = ""
    private var requestedMatchId: String? = null
    private var requestedToken: Int = 0
    private var openedToken: Int = 0
    private var listJob: Job? = null
    private var messagesJob: Job? = null

    fun start(userId: String) {
        if (userId.isBlank()) {
            this.userId = ""
            _state.value = ChatsUiState(loading = false, error = "Нужно войти в аккаунт")
            return
        }
        if (this.userId == userId && listJob != null) return
        this.userId = userId
        signaled.clear()
        listJob?.cancel()
        listJob = viewModelScope.launch {
            try {
                observeChats(userId).collect { chats ->
                    _state.update { state ->
                        val opened = state.opened?.let { current ->
                            chats.firstOrNull { it.id == current.summary.id }
                                ?.let { current.copy(summary = it) }
                                ?: current
                        }
                        state.copy(loading = false, error = null, chats = chats, opened = opened)
                    }
                    openRequested()
                    announce(chats)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _state.update { it.copy(loading = false, error = error.message ?: "Не удалось выполнить запрос") }
            }
        }
    }

    fun focus(matchId: String?, token: Int) {
        if (matchId.isNullOrBlank() || token == 0 || token == openedToken) return
        requestedMatchId = matchId
        requestedToken = token
        openRequested()
    }

    fun select(summary: ChatSummary) {
        _state.update { it.copy(opened = OpenedChat(summary), error = null) }
        viewModelScope.launch {
            openChat(summary.id, summary.matchId).onFailure { error ->
                _state.update { it.copy(error = error.message ?: "Не удалось открыть чат") }
            }
        }
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch {
            try {
                observeMessages(summary.id, userId).collect { messages ->
                    _state.update { state ->
                        val opened = state.opened ?: return@update state
                        if (opened.summary.id != summary.id) state
                        else state.copy(opened = opened.copy(messages = messages))
                    }
                    val unread = messages.filter { !it.mine && !it.isRead }.map { it.id }
                    if (unread.isNotEmpty()) markRead(summary.id, unread)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _state.update { it.copy(error = error.message ?: "Не удалось выполнить запрос") }
            }
        }
    }

    fun draft(value: String) {
        _state.update { state ->
            val opened = state.opened ?: return@update state
            state.copy(opened = opened.copy(draft = value))
        }
    }

    fun send() {
        val opened = _state.value.opened ?: return
        if (opened.sending || opened.draft.isBlank()) return
        viewModelScope.launch {
            _state.update { state -> state.copy(opened = state.opened?.copy(sending = true)) }
            sendMessage(opened.summary.id, userId, opened.draft).fold(
                onSuccess = {
                    _state.update { state ->
                        state.copy(opened = state.opened?.copy(draft = "", sending = false))
                    }
                },
                onFailure = { error ->
                    _state.update { state ->
                        state.copy(
                            error = error.message ?: "Не удалось отправить сообщение",
                            opened = state.opened?.copy(sending = false)
                        )
                    }
                }
            )
        }
    }

    fun closeThread() {
        messagesJob?.cancel()
        requestedMatchId = null
        _state.update { it.copy(opened = null) }
    }

    private fun announce(chats: List<ChatSummary>) {
        val openedId = _state.value.opened?.summary?.id
        chats.forEach { chat ->
            val last = chat.lastMessage ?: return@forEach
            if (last.mine || last.isRead || chat.id == openedId) return@forEach
            if (!signaled.add(last.id)) return@forEach
            _incoming.tryEmit(
                IncomingChat(
                    matchId = chat.matchId,
                    messageId = last.id,
                    peerName = chat.peerName,
                    peerUsername = chat.peerUsername
                )
            )
        }
    }

    private fun openRequested() {
        val matchId = requestedMatchId ?: return
        if (requestedToken == openedToken) return
        val summary = _state.value.chats.firstOrNull { it.matchId == matchId || it.id == matchId } ?: return
        requestedMatchId = null
        openedToken = requestedToken
        if (_state.value.opened?.summary?.matchId == summary.matchId) return
        select(summary)
    }
}
