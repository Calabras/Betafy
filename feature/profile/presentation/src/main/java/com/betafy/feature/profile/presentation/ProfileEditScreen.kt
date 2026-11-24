package com.betafy.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.betafy.betafyapp.core.uikit.theme.BetafySizes
import com.betafy.betafyapp.core.uikit.theme.BetafySpacing
import com.betafy.feature.profile.domain.model.ProfileChoices

@Composable
fun ProfileEditScreen(
    kind: ProfileKind,
    userId: String?,
    onBack: () -> Unit,
    onSaved: (String) -> Unit,
    viewModel: ProfileEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(kind, userId) {
        viewModel.open(kind, userId)
    }
    LaunchedEffect(state.saved) {
        if (state.saved) {
            val text = if (kind == ProfileKind.READER) {
                "Профиль бета-ридера создан успешно"
            } else {
                "Профиль писателя создан успешно"
            }
            onSaved(text)
        }
    }

    val title = if (kind == ProfileKind.READER) "Профиль ридера" else "Профиль писателя"
    val bioLabel = if (kind == ProfileKind.READER) "О себе" else "Об авторе"
    val experienceLabel = if (kind == ProfileKind.READER) "Опыт бета-чтения" else "Опыт писательства"
    val languageLabel = if (kind == ProfileKind.READER) "Язык чтения" else "Язык произведений"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад"
                )
            }
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        }

        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            !state.loaded -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = BetafySpacing.screenHorizontal),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.error ?: "Не удалось открыть профиль",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(BetafySpacing.sm))
                    TextButton(onClick = { viewModel.retry(kind, userId) }) {
                        Text("Повторить")
                    }
                }
            }

            else -> {
                ProfileForm(
                    kind = kind,
                    state = state,
                    bioLabel = bioLabel,
                    experienceLabel = experienceLabel,
                    languageLabel = languageLabel,
                    onBioChange = viewModel::onBioChange,
                    onExperienceChange = viewModel::onExperienceChange,
                    onLanguageChange = viewModel::onLanguageChange,
                    onMaxWordCountChange = viewModel::onMaxWordCountChange,
                    onAvailableChange = viewModel::onAvailableChange,
                    onCooperationChange = viewModel::onCooperationChange,
                    onPriceChange = viewModel::onPriceChange,
                    onToggleGenre = viewModel::toggleGenre,
                    onToggleFeedback = viewModel::toggleFeedbackType,
                    onSave = viewModel::save
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileForm(
    kind: ProfileKind,
    state: ProfileEditUiState,
    bioLabel: String,
    experienceLabel: String,
    languageLabel: String,
    onBioChange: (String) -> Unit,
    onExperienceChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onMaxWordCountChange: (String) -> Unit,
    onAvailableChange: (Boolean) -> Unit,
    onCooperationChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onToggleGenre: (String) -> Unit,
    onToggleFeedback: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = BetafySpacing.screenHorizontal,
                vertical = BetafySpacing.md
            ),
        verticalArrangement = Arrangement.spacedBy(BetafySpacing.sm)
    ) {
        ProfileTextField(
            value = state.bio,
            onValueChange = onBioChange,
            label = bioLabel,
            singleLine = false,
            modifier = Modifier.heightIn(min = 120.dp)
        )
        ChoiceField(
            label = experienceLabel,
            value = state.experienceLevel,
            options = optionsWithCurrent(ProfileChoices.experienceLevels, state.experienceLevel),
            onSelect = onExperienceChange
        )
        ChoiceField(
            label = languageLabel,
            value = state.preferredLanguage,
            options = optionsWithCurrent(ProfileChoices.languages, state.preferredLanguage),
            onSelect = onLanguageChange
        )

        if (kind == ProfileKind.READER) {
            ProfileTextField(
                value = state.maxWordCount,
                onValueChange = onMaxWordCountChange,
                label = "Максимальный объём (слов)",
                keyboardType = KeyboardType.Number
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Готов брать работы", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = state.isAvailable,
                    onCheckedChange = onAvailableChange
                )
            }
            ChoiceField(
                label = "Формат сотрудничества",
                value = state.cooperationType,
                options = optionsWithCurrent(ProfileChoices.cooperationTypes, state.cooperationType),
                onSelect = onCooperationChange
            )
            if (state.cooperationType == ProfileChoices.PAID) {
                ProfileTextField(
                    value = state.price,
                    onValueChange = onPriceChange,
                    label = "Цена за платное чтение",
                    keyboardType = KeyboardType.Decimal
                )
            }
            MultiChoice(
                title = "Жанры",
                emptyText = "Справочник жанров пока пуст",
                items = state.genres.map { it.id to it.name },
                selected = state.selectedGenreIds,
                onToggle = onToggleGenre
            )
            MultiChoice(
                title = "Компетенции",
                emptyText = "Справочник видов обратной связи пока пуст",
                items = state.feedbackTypes.map { it.id to it.name },
                selected = state.selectedFeedbackTypeIds,
                onToggle = onToggleFeedback
            )
        }

        val existsText = if (state.alreadyExists) {
            if (kind == ProfileKind.READER) "Профиль бета-ридера уже создан" else "Профиль писателя уже создан"
        } else {
            null
        }
        val missing = if (existsText == null) state.inputProblem() else null
        if (existsText != null) {
            Text(
                text = existsText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (state.error != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Text(
                    text = state.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        } else if (missing != null) {
            Text(
                text = missing,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = onSave,
            enabled = state.loaded && !state.isSaving && !state.alreadyExists && missing == null,
            modifier = Modifier
                .fillMaxWidth()
                .height(BetafySizes.buttonHeight)
        ) {
            if (state.isSaving) {
                CircularProgressIndicator()
            } else {
                Text("Сохранить", style = MaterialTheme.typography.titleMedium)
            }
        }
        Spacer(Modifier.height(BetafySpacing.lg))
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val colors = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 4,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surfaceContainerLow,
            unfocusedContainerColor = colors.surfaceContainerLow,
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.outlineVariant,
            focusedLabelColor = colors.primary,
            cursorColor = colors.primary
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChoiceField(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colors.surfaceContainerLow,
                unfocusedContainerColor = colors.surfaceContainerLow,
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.outlineVariant,
                focusedLabelColor = colors.primary,
                cursorColor = colors.primary
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MultiChoice(
    title: String,
    emptyText: String,
    items: List<Pair<String, String>>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Text(text = title, style = MaterialTheme.typography.titleSmall)
    if (items.isEmpty()) {
        Text(
            text = emptyText,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    } else {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(BetafySpacing.xs),
            verticalArrangement = Arrangement.spacedBy(BetafySpacing.xs)
        ) {
            items.forEach { (id, name) ->
                FilterChip(
                    selected = id in selected,
                    onClick = { onToggle(id) },
                    label = { Text(name) }
                )
            }
        }
    }
}

private fun optionsWithCurrent(options: List<String>, current: String): List<String> {
    if (current.isBlank() || current in options) return options
    return listOf(current) + options
}
