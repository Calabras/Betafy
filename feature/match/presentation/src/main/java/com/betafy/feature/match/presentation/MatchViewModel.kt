package com.betafy.feature.match.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betafy.feature.match.domain.LoadMatchBoardUseCase
import com.betafy.feature.match.domain.ProposeMatchUseCase
import com.betafy.feature.match.domain.model.MatchLine
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MatchUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val notice: String? = null,
    val hasReaderProfile: Boolean = false,
    val hasOpenRequests: Boolean = false,
    val writerLines: List<MatchLine> = emptyList(),
    val readerLines: List<MatchLine> = emptyList(),
    val pendingKey: String? = null
)

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val loadBoard: LoadMatchBoardUseCase,
    private val propose: ProposeMatchUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MatchUiState())
    val state: StateFlow<MatchUiState> = _state.asStateFlow()
    private var userId: String = ""

    fun open(userId: String) {
        this.userId = userId
        reload()
    }

    fun reload() {
        if (userId.isBlank()) {
            _state.value = MatchUiState(loading = false, error = "Нужно войти в аккаунт")
            return
        }
        viewModelScope.launch { load(notice = null) }
    }

    fun propose(line: MatchLine) {
        if (_state.value.pendingKey != null) return
        viewModelScope.launch {
            _state.update { it.copy(pendingKey = line.key, error = null, notice = null) }
            propose(userId, line.requestId, line.readerId).fold(
                onSuccess = {
                    load(notice = if (line.writerSide) "Приглашение отправлено" else "Отклик отправлен")
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(pendingKey = null, error = error.message ?: "Не удалось выполнить запрос")
                    }
                }
            )
        }
    }

    private suspend fun load(notice: String?) {
        _state.update { it.copy(loading = true, error = null, notice = notice, pendingKey = null) }
        loadBoard(userId).fold(
            onSuccess = { board ->
                _state.value = MatchUiState(
                    loading = false,
                    notice = notice,
                    hasReaderProfile = board.hasReaderProfile,
                    hasOpenRequests = board.hasOpenRequests,
                    writerLines = board.writerLines,
                    readerLines = board.readerLines
                )
            },
            onFailure = { error ->
                _state.value = MatchUiState(
                    loading = false,
                    error = error.message ?: "Не удалось выполнить запрос"
                )
            }
        )
    }
}
