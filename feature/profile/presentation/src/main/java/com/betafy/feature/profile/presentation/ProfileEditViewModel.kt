package com.betafy.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.betafy.feature.profile.domain.GetFeedbackTypesUseCase
import com.betafy.feature.profile.domain.GetGenresUseCase
import com.betafy.feature.profile.domain.GetReaderProfileUseCase
import com.betafy.feature.profile.domain.GetWriterProfileUseCase
import com.betafy.feature.profile.domain.ProfileTrace
import com.betafy.feature.profile.domain.SaveReaderProfileUseCase
import com.betafy.feature.profile.domain.SaveWriterProfileUseCase
import com.betafy.feature.profile.domain.model.FeedbackType
import com.betafy.feature.profile.domain.model.Genre
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val getReaderProfile: GetReaderProfileUseCase,
    private val saveReaderProfile: SaveReaderProfileUseCase,
    private val getWriterProfile: GetWriterProfileUseCase,
    private val saveWriterProfile: SaveWriterProfileUseCase,
    private val getGenres: GetGenresUseCase,
    private val getFeedbackTypes: GetFeedbackTypesUseCase,
    private val trace: ProfileTrace
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileEditUiState())
    val state: StateFlow<ProfileEditUiState> = _state.asStateFlow()

    private var opened = false

    fun open(kind: ProfileKind, userId: String?) {
        if (opened) return
        opened = true
        trace.event("экран ${kind.name} открыт, user=${userId ?: "нет"}")
        if (userId.isNullOrBlank()) {
            _state.value = ProfileEditUiState(
                kind = kind,
                error = "Нужно войти в аккаунт"
            )
            return
        }
        _state.update { it.copy(kind = kind, userId = userId, isLoading = true, error = null) }
        viewModelScope.launch {
            val genres = getGenres().getOrDefault(emptyList())
            val feedbackTypes = getFeedbackTypes().getOrDefault(emptyList())
            if (kind == ProfileKind.READER) {
                getReaderProfile(userId)
                    .onSuccess { profile ->
                        _state.update { current ->
                            current.copy(
                                isLoading = false,
                                loaded = true,
                                alreadyExists = profile != null,
                                genres = genres,
                                feedbackTypes = feedbackTypes
                            )
                        }
                        trace.event("экран READER загружен, запись=${if (profile == null) "нет" else "есть"}")
                    }
                    .onFailure { error -> showLoadFailure(error, genres, feedbackTypes) }
            } else {
                getWriterProfile(userId)
                    .onSuccess { profile ->
                        _state.update { current ->
                            current.copy(
                                isLoading = false,
                                loaded = true,
                                alreadyExists = profile != null
                            )
                        }
                        trace.event("экран WRITER загружен, запись=${if (profile == null) "нет" else "есть"}")
                    }
                    .onFailure { error -> showLoadFailure(error, emptyList(), emptyList()) }
            }
        }
    }

    private fun showLoadFailure(
        error: Throwable,
        genres: List<Genre>,
        feedbackTypes: List<FeedbackType>
    ) {
        val message = error.message
        trace.event("загрузка ${_state.value.kind.name}: ${message ?: "без текста"}")
        val rulesClosed = message?.contains("правила базы", ignoreCase = true) == true
        _state.update {
            it.copy(
                isLoading = false,
                loaded = rulesClosed,
                genres = genres,
                feedbackTypes = feedbackTypes,
                error = message
            )
        }
    }

    fun onBioChange(value: String) = _state.update { it.copy(bio = value, error = null) }

    fun onExperienceChange(value: String) = _state.update { it.copy(experienceLevel = value, error = null) }

    fun onLanguageChange(value: String) = _state.update { it.copy(preferredLanguage = value, error = null) }

    fun onMaxWordCountChange(value: String) = _state.update {
        it.copy(maxWordCount = value.filter(Char::isDigit).take(7), error = null)
    }

    fun onAvailableChange(value: Boolean) = _state.update { it.copy(isAvailable = value, error = null) }

    fun onCooperationChange(value: String) = _state.update { it.copy(cooperationType = value, error = null) }

    fun onPriceChange(value: String) = _state.update {
        it.copy(price = sanitizePrice(value), error = null)
    }

    fun toggleGenre(id: String) = _state.update { state ->
        val next = state.selectedGenreIds.toMutableSet()
        if (!next.add(id)) next.remove(id)
        state.copy(selectedGenreIds = next, error = null)
    }

    fun toggleFeedbackType(id: String) = _state.update { state ->
        val next = state.selectedFeedbackTypeIds.toMutableSet()
        if (!next.add(id)) next.remove(id)
        state.copy(selectedFeedbackTypeIds = next, error = null)
    }

    fun save() {
        val current = _state.value
        if (!current.loaded || current.isSaving || current.alreadyExists) return
        val problem = current.inputProblem()
        if (problem != null) {
            trace.event("сохранение ${current.kind.name} отклонено: $problem")
            _state.update { it.copy(error = problem) }
            return
        }
        trace.event("сохранение ${current.kind.name} user=${current.userId}: старт")
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val result = if (current.kind == ProfileKind.READER) {
                saveReaderProfile(
                    current.toReader(),
                    current.genres.size,
                    current.feedbackTypes.size
                )
            } else {
                saveWriterProfile(current.toWriter())
            }
            result
                .onSuccess {
                    trace.event("сохранение ${current.kind.name} user=${current.userId}: успех")
                    _state.update {
                        it.copy(
                            isSaving = false,
                            saved = true,
                            bio = "",
                            experienceLevel = "",
                            preferredLanguage = "",
                            maxWordCount = "",
                            isAvailable = false,
                            cooperationType = "",
                            price = "",
                            selectedGenreIds = emptySet(),
                            selectedFeedbackTypeIds = emptySet(),
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    trace.event("сохранение ${current.kind.name} user=${current.userId}: ${error.message}")
                    val detail = error.message?.takeIf { it.isNotBlank() }
                    val text = if (detail == null) {
                        "Профиль не был создан, произошла ошибка"
                    } else {
                        "Профиль не был создан, произошла ошибка\n$detail"
                    }
                    _state.update { it.copy(isSaving = false, error = text) }
                }
        }
    }

    fun retry(kind: ProfileKind, userId: String?) {
        opened = false
        open(kind, userId)
    }

    private fun sanitizePrice(value: String): String {
        val cleaned = value.replace(',', '.').filter { it.isDigit() || it == '.' }
        val dot = cleaned.indexOf('.')
        val singleDot = if (dot < 0) {
            cleaned
        } else {
            cleaned.substring(0, dot + 1) + cleaned.substring(dot + 1).replace(".", "")
        }
        return singleDot.take(8)
    }
}
