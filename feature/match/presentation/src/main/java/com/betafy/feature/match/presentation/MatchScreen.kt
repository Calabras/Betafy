package com.betafy.feature.match.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.betafy.feature.match.domain.model.MatchLine
import com.betafy.feature.match.domain.model.MatchTrait
import kotlin.math.roundToInt

private val SharedGreen = Color(0xFF336A28)

@Composable
fun MatchScreen(
    userId: String,
    viewModel: MatchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var lookingForReader by remember { mutableIntStateOf(0) }
    LaunchedEffect(userId) { viewModel.open(userId) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp)
    ) {
        SeekSwitch(
            selected = lookingForReader,
            onSelect = { lookingForReader = it }
        )
        Spacer(Modifier.height(12.dp))
        if (lookingForReader == 1) {
            Placeholder("Поиск писателя скоро появится")
        } else {
            WriterSeek(
                state = state,
                onApprove = viewModel::propose,
                onReject = viewModel::skip,
                onReload = viewModel::reload
            )
        }
    }
}

@Composable
private fun WriterSeek(
    state: MatchUiState,
    onApprove: (MatchLine) -> Unit,
    onReject: (MatchLine) -> Unit,
    onReload: () -> Unit
) {
    when {
        state.loading && state.writerLines.isEmpty() -> Placeholder(loading = true)
        state.error != null && state.writerLines.isEmpty() -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.error.orEmpty(), color = MaterialTheme.colorScheme.error)
                TextButton(onClick = onReload) { Text("Повторить") }
            }
        }
        state.writerLines.isEmpty() -> {
            Placeholder(
                text = if (state.hasOpenRequests) {
                    "Подходящих ридеров пока нет"
                } else {
                    "Сначала откройте запрос на бета-чтение"
                }
            )
        }
        else -> {
            val line = state.writerLines.first()
            Column(modifier = Modifier.fillMaxSize()) {
                state.notice?.let { notice ->
                    Text(
                        text = notice,
                        color = SharedGreen,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Text(
                    text = "Вправо пригласить, влево пропустить",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                SwipeCard(
                    line = line,
                    enabled = state.pendingKey == null,
                    onApprove = { onApprove(line) },
                    onReject = { onReject(line) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SwipeCard(
    line: MatchLine,
    enabled: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val threshold = with(density) { 96.dp.toPx() }
    var drag by remember(line.key) { mutableFloatStateOf(0f) }
    val tint = when {
        drag > 24f -> SharedGreen.copy(alpha = 0.16f)
        drag < -24f -> MaterialTheme.colorScheme.error.copy(alpha = 0.14f)
        else -> Color.Transparent
    }
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(drag.roundToInt(), 0) }
                .graphicsLayer { rotationZ = (drag / 40f).coerceIn(-12f, 12f) }
                .pointerInput(line.key, enabled) {
                    if (!enabled) return@pointerInput
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            when {
                                drag > threshold -> onApprove()
                                drag < -threshold -> onReject()
                                else -> drag = 0f
                            }
                        },
                        onDragCancel = { drag = 0f },
                        onHorizontalDrag = { _, amount -> drag += amount }
                    )
                },
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shadowElevation = 2.dp
        ) {
            Box(Modifier.background(tint)) {
                ReaderCardBody(line)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReaderCardBody(line: MatchLine) {
    val name = listOf(line.firstName, line.lastName)
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { line.counterpart }
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "${line.score}%",
            style = MaterialTheme.typography.labelLarge,
            color = SharedGreen,
            fontWeight = FontWeight.Bold
        )
        Text(text = name, style = MaterialTheme.typography.headlineSmall)
        if (line.traits.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                line.traits.forEach { trait -> TraitChip(trait) }
            }
        }
    }
}

@Composable
private fun TraitChip(trait: MatchTrait) {
    val border = if (trait.shared) SharedGreen else MaterialTheme.colorScheme.outlineVariant
    val fill = if (trait.shared) SharedGreen.copy(alpha = 0.12f) else Color.Transparent
    Surface(
        shape = RoundedCornerShape(50),
        color = fill,
        border = BorderStroke(if (trait.shared) 1.5.dp else 1.dp, border)
    ) {
        Text(
            text = trait.label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            color = if (trait.shared) SharedGreen else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SeekSwitch(
    selected: Int,
    onSelect: (Int) -> Unit
) {
    val labels = listOf("Я ищу ридера", "Я ищу писателя")
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceContainer, MaterialTheme.shapes.medium)
            .padding(4.dp)
    ) {
        labels.forEachIndexed { index, label ->
            val active = selected == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(if (active) colors.primary else Color.Transparent)
                    .selectable(
                        selected = active,
                        role = Role.Tab,
                        onClick = { onSelect(index) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (active) colors.onPrimary else colors.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun Placeholder(
    text: String = "",
    loading: Boolean = false
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (loading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
