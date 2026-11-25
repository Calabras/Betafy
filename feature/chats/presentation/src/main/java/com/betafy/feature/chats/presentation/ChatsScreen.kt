package com.betafy.feature.chats.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.betafy.betafyapp.core.uikit.theme.extendedColors
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.betafy.feature.chats.domain.model.ChatMessage
import com.betafy.feature.chats.domain.model.ChatSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatsScreen(
    userId: String,
    focusMatchId: String? = null,
    focusToken: Int = 0,
    viewModel: ChatsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(userId) { viewModel.start(userId) }
    LaunchedEffect(focusMatchId, focusToken, state.chats, state.loading) {
        viewModel.focus(focusMatchId, focusToken)
    }
    val opened = state.opened
    if (opened == null) {
        ChatList(
            loading = state.loading,
            error = state.error,
            chats = state.chats,
            onOpen = viewModel::select
        )
    } else {
        ChatThread(
            opened = opened,
            error = state.error,
            onBack = viewModel::closeThread,
            onDraft = viewModel::draft,
            onSend = viewModel::send
        )
    }
}

@Composable
private fun ChatList(
    loading: Boolean,
    error: String?,
    chats: List<ChatSummary>,
    onOpen: (ChatSummary) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            chats.isEmpty() -> Text(
                text = error ?: "Чатов пока нет",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Center).padding(24.dp)
            )
            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (!error.isNullOrBlank()) {
                    item {
                        Text(
                            error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                }
                items(chats, key = { it.id }) { chat ->
                    ChatRow(chat, onClick = { onOpen(chat) })
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun ChatRow(chat: ChatSummary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(chat.peerName, 40.dp)
        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = chat.peerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (chat.unread > 0) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                chat.lastMessage?.takeIf { it.sentAt > 0L }?.let { message ->
                    Text(
                        text = clock(message.sentAt),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (chat.peerUsername.isNotBlank()) "@${chat.peerUsername}" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                chat.lastMessage?.takeIf { it.sentAt > 0L }?.let { message ->
                    Text(
                        text = day(message.sentAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = chat.lastMessage?.text ?: "Нет сообщений",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (chat.unread > 0) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = if (chat.unread > 0) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (chat.unread > 0) {
                    UnreadBadge(chat.unread)
                } else {
                    chat.lastMessage?.takeIf { it.mine }?.let { message ->
                        ReadMarks(message.isRead, MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatThread(
    opened: OpenedChat,
    error: String?,
    onBack: () -> Unit,
    onDraft: (String) -> Unit,
    onSend: () -> Unit
) {
    val listState = rememberLazyListState()
    LaunchedEffect(opened.messages.size) {
        if (opened.messages.isNotEmpty()) listState.animateScrollToItem(opened.messages.lastIndex)
    }
    val mine = MaterialTheme.extendedColors.success
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
            Avatar(opened.summary.peerName, 40.dp)
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = opened.summary.peerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (opened.summary.peerUsername.isNotBlank()) {
                    Text(
                        text = "@${opened.summary.peerUsername}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        Box(modifier = Modifier.weight(1f)) {
            if (opened.messages.isEmpty()) {
                Text(
                    text = "Напишите первое сообщение",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                items(opened.messages, key = { it.id }) { message ->
                    MessageBubble(message)
                }
            }
        }
        if (!error.isNullOrBlank()) {
            Text(
                error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = opened.draft,
                onValueChange = onDraft,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Сообщение") },
                shape = MaterialTheme.shapes.extraLarge,
                keyboardOptions = KeyboardOptions(),
                minLines = 1,
                maxLines = 5
            )
            val canSend = opened.draft.isNotBlank() && !opened.sending
            IconButton(
                onClick = onSend,
                enabled = canSend,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (canSend) mine.color else MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Отправить",
                    tint = if (canSend) mine.onColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val mine = MaterialTheme.extendedColors.success
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.mine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = MaterialTheme.shapes.large,
            color = if (message.mine) mine.colorContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = if (message.mine) mine.onColorContainer else MaterialTheme.colorScheme.onSurface
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(text = message.text)
                if (message.sentAt > 0L || message.mine) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (message.sentAt > 0L) {
                            Text(
                                text = "${clock(message.sentAt)}  ${day(message.sentAt)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (message.mine) mine.onColorContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (message.mine) {
                            ReadMarks(read = message.isRead, tint = mine.onColorContainer)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Avatar(name: String, size: Dp) {
    val letter = name.trim().firstOrNull()?.uppercase() ?: "?"
    val mint = MaterialTheme.extendedColors.success
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(mint.colorContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(text = letter, color = mint.onColorContainer, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun UnreadBadge(count: Int) {
    val label = if (count > 99) "99+" else count.toString()
    val mint = MaterialTheme.extendedColors.success
    Box(
        modifier = Modifier
            .padding(start = 8.dp)
            .size(if (label.length > 2) 26.dp else 22.dp)
            .clip(CircleShape)
            .background(mint.color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = mint.onColor,
            fontSize = if (label.length > 2) 9.sp else 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun ReadMarks(read: Boolean, tint: Color) {
    Box(
        modifier = Modifier
            .padding(start = 4.dp)
            .width(if (read) 22.dp else 16.dp)
            .height(16.dp)
            .semantics { contentDescription = if (read) "Прочитано" else "Отправлено" }
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = tint,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(16.dp)
        )
        if (read) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = tint,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(16.dp)
            )
        }
    }
}

private val stampLocale: Locale = Locale.forLanguageTag("ru")

private fun clock(millis: Long): String =
    SimpleDateFormat("HH:mm", stampLocale).format(Date(millis))

private fun day(millis: Long): String =
    SimpleDateFormat("dd.MM.yyyy", stampLocale).format(Date(millis))
