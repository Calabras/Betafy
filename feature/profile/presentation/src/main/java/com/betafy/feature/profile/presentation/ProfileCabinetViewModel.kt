package com.betafy.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betafy.feature.profile.domain.GetReaderProfileUseCase
import com.betafy.feature.profile.domain.GetWriterProfileUseCase
import com.betafy.feature.profile.domain.ProfileTrace
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileCabinetViewModel @Inject constructor(
    private val getReaderProfile: GetReaderProfileUseCase,
    private val getWriterProfile: GetWriterProfileUseCase,
    private val trace: ProfileTrace
) : ViewModel() {

    data class State(
        val checked: Boolean = false,
        val readerExists: Boolean = false,
        val writerExists: Boolean = false
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun refresh(userId: String?) {
        if (userId.isNullOrBlank()) {
            _state.value = State(checked = true)
            return
        }
        _state.value = State()
        viewModelScope.launch {
            val reader = getReaderProfile(userId)
            val writer = getWriterProfile(userId)
            val readerExists = reader.getOrNull() != null
            val writerExists = writer.getOrNull() != null
            trace.event(
                "проверка профилей user=$userId " +
                    "ридер=${resultLabel(readerExists, reader.isFailure)} " +
                    "писатель=${resultLabel(writerExists, writer.isFailure)}"
            )
            _state.value = State(
                checked = true,
                readerExists = readerExists,
                writerExists = writerExists
            )
        }
    }

    private fun resultLabel(exists: Boolean, failed: Boolean): String = when {
        failed -> "ошибка чтения"
        exists -> "есть"
        else -> "нет"
    }
}
