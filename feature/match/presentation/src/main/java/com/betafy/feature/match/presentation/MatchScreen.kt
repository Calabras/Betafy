package com.betafy.feature.match.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.betafy.feature.match.domain.model.MatchLine

@Composable
fun MatchScreen(
    userId: String,
    viewModel: MatchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(userId) { viewModel.open(userId) }
    when {
        state.loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
        state.error != null && state.writerLines.isEmpty() && state.readerLines.isEmpty() -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.error.orEmpty(), color = MaterialTheme.colorScheme.error)
                TextButton(onClick = viewModel::reload) { Text("Повторить") }
            }
        }
        else -> MatchList(state = state, onPropose = viewModel::propose, onReload = viewModel::reload)
    }
}

@Composable
private fun MatchList(
    state: MatchUiState,
    onPropose: (MatchLine) -> Unit,
    onReload: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        state.notice?.let { notice ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Text(text = notice, modifier = Modifier.padding(12.dp))
            }
        }
        state.error?.let { error ->
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }
        if (state.hasOpenRequests) {
            Text(text = "Подходящие ридеры", style = MaterialTheme.typography.titleMedium)
            if (state.writerLines.isEmpty()) {
                Text(text = "Для ваших запросов пока нет подходящих ридеров")
            }
            state.writerLines.forEach { line ->
                MatchCard(line = line, action = "Пригласить", pending = state.pendingKey == line.key, onPropose = onPropose)
            }
        }
        if (state.hasReaderProfile) {
            Text(text = "Подходящие запросы", style = MaterialTheme.typography.titleMedium)
            if (state.readerLines.isEmpty()) {
                Text(text = "Для вашей анкеты пока нет подходящих запросов")
            }
            state.readerLines.forEach { line ->
                MatchCard(line = line, action = "Откликнуться", pending = state.pendingKey == line.key, onPropose = onPropose)
            }
        }
        if (!state.hasOpenRequests && !state.hasReaderProfile) {
            Text(text = "Создайте профиль ридера или запрос на бета-чтение, тогда здесь появится подбор")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onReload) { Text("Обновить") }
    }
}

@Composable
private fun MatchCard(
    line: MatchLine,
    action: String,
    pending: Boolean,
    onPropose: (MatchLine) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = line.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "${line.counterpart} · ${line.score}%")
            if (line.caption.isNotBlank()) {
                Text(text = line.caption, style = MaterialTheme.typography.bodySmall)
            }
            Button(
                onClick = { onPropose(line) },
                enabled = !pending,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (pending) "Отправка" else action)
            }
        }
    }
}
