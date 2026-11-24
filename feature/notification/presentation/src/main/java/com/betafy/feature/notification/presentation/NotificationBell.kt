package com.betafy.feature.notification.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.betafy.betafyapp.core.uikit.theme.BetafySizes
import com.betafy.feature.notification.domain.model.InboxNotification
import com.betafy.feature.notification.domain.unreadBadge

private sealed interface NoticePanel {
    data object Inbox : NoticePanel
    data class Read(val item: InboxNotification) : NoticePanel
    data class ConfirmDelete(val ids: List<String>, val message: String) : NoticePanel
}

@Composable
fun NotificationBell(
    accent: Color,
    viewModel: NotificationInboxViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var opened by remember { mutableStateOf(false) }
    val label = unreadBadge(state.unread)
    val buttonColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val iconColor = MaterialTheme.colorScheme.onSurface
    val onAccent = if (accent.luminance() > 0.5f) Color(0xFF1C1B1F) else Color.White
    val badgeSize = if (label != null && label.length > 2) 28.dp else 22.dp

    Box {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(buttonColor)
                .clickable { opened = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = if (label == null) {
                    "Уведомления"
                } else {
                    "Уведомления, непрочитанных $label"
                },
                tint = iconColor
            )
        }
        if (label != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-6).dp)
                    .size(badgeSize)
                    .clip(CircleShape)
                    .background(accent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = onAccent,
                    fontSize = if (label.length > 2) 9.sp else 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    lineHeight = 12.sp
                )
            }
        }
    }

    if (opened) {
        NotificationWindow(
            accent = accent,
            state = state,
            onDismiss = { opened = false },
            onMarkRead = viewModel::markRead,
            onDelete = viewModel::delete
        )
    }
}

@Composable
private fun NotificationWindow(
    accent: Color,
    state: NotificationUiState,
    onDismiss: () -> Unit,
    onMarkRead: (List<String>) -> Unit,
    onDelete: (List<String>) -> Unit
) {
    var panel by remember { mutableStateOf<NoticePanel>(NoticePanel.Inbox) }
    var selected by remember { mutableStateOf(setOf<String>()) }
    val ids = state.items.map { it.id }.toSet()
    val chosen = selected.intersect(ids)

    Dialog(
        onDismissRequest = {
            if (panel is NoticePanel.Inbox) onDismiss() else panel = NoticePanel.Inbox
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .heightIn(max = 640.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            when (val current = panel) {
                NoticePanel.Inbox -> InboxPage(
                    accent = accent,
                    state = state,
                    chosen = chosen,
                    onChecked = { id, on ->
                        selected = if (on) selected + id else selected - id
                    },
                    onOpen = { panel = NoticePanel.Read(it) },
                    onMarkRead = { target ->
                        onMarkRead(target)
                        selected = emptySet()
                    },
                    onAskDelete = { target, message ->
                        panel = NoticePanel.ConfirmDelete(target, message)
                    }
                )
                is NoticePanel.Read -> ReadPage(
                    item = current.item,
                    onMarkRead = onMarkRead,
                    onClose = { panel = NoticePanel.Inbox },
                    onAskDelete = {
                        panel = NoticePanel.ConfirmDelete(
                            listOf(current.item.id),
                            "Удалить это уведомление"
                        )
                    }
                )
                is NoticePanel.ConfirmDelete -> DeletePage(
                    message = current.message,
                    onKeep = { panel = NoticePanel.Inbox },
                    onDelete = {
                        onDelete(current.ids)
                        selected = emptySet()
                        panel = NoticePanel.Inbox
                    }
                )
            }
        }
    }
}

@Composable
private fun InboxPage(
    accent: Color,
    state: NotificationUiState,
    chosen: Set<String>,
    onChecked: (String, Boolean) -> Unit,
    onOpen: (InboxNotification) -> Unit,
    onMarkRead: (List<String>) -> Unit,
    onAskDelete: (List<String>, String) -> Unit
) {
    val markLabel = if (chosen.isEmpty()) "Прочитать все" else "Прочитать выбранные"
    val deleteLabel = if (chosen.isEmpty()) "Удалить все" else "Удалить выбранные"
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 8.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Уведомления",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {
                    val target = if (chosen.isEmpty()) state.items.map { it.id } else chosen.toList()
                    onMarkRead(target)
                },
                enabled = state.items.isNotEmpty()
            ) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = markLabel)
            }
            IconButton(
                onClick = {
                    val target = if (chosen.isEmpty()) state.items.map { it.id } else chosen.toList()
                    val message = if (chosen.isEmpty()) "Удалить все уведомления" else "Удалить выбранные уведомления"
                    onAskDelete(target, message)
                },
                enabled = state.items.isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = deleteLabel,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        Text(
            text = if (chosen.isEmpty()) "Прочитать все, удалить все" else "Выбрано ${chosen.size}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }
        when {
            state.loading && state.items.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = accent)
                }
            }
            state.items.isEmpty() -> {
                Text(
                    text = "Уведомлений нет",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                )
            }
            else -> {
                LazyColumn(modifier = Modifier.heightIn(max = 460.dp)) {
                    items(state.items, key = { it.id }) { item ->
                        NotificationRow(
                            item = item,
                            accent = accent,
                            checked = item.id in chosen,
                            onChecked = { onChecked(item.id, it) },
                            onOpen = { onOpen(item) },
                            onDelete = { onAskDelete(listOf(item.id), "Удалить это уведомление") }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadPage(
    item: InboxNotification,
    onMarkRead: (List<String>) -> Unit,
    onClose: () -> Unit,
    onAskDelete: () -> Unit
) {
    LaunchedEffect(item.id) {
        if (!item.isRead) onMarkRead(listOf(item.id))
    }
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = titleOf(item.type), style = MaterialTheme.typography.titleLarge)
        Text(text = item.text, style = MaterialTheme.typography.bodyLarge)
        CardButton(text = "Закрыть", onClick = onClose)
        TextButton(
            onClick = onAskDelete,
            modifier = Modifier
                .fillMaxWidth()
                .height(BetafySizes.buttonHeight),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Удалить", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun DeletePage(
    message: String,
    onKeep: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Удаление", style = MaterialTheme.typography.titleLarge)
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        CardButton(
            text = "Удалить",
            onClick = onDelete,
            container = MaterialTheme.colorScheme.error,
            content = MaterialTheme.colorScheme.onError
        )
        TextButton(
            onClick = onKeep,
            modifier = Modifier
                .fillMaxWidth()
                .height(BetafySizes.buttonHeight),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Оставить")
        }
    }
}

@Composable
private fun CardButton(
    text: String,
    onClick: () -> Unit,
    container: Color = MaterialTheme.colorScheme.primary,
    content: Color = MaterialTheme.colorScheme.onPrimary
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(BetafySizes.buttonHeight),
        shape = MaterialTheme.shapes.medium,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp
        ),
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun NotificationRow(
    item: InboxNotification,
    accent: Color,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onChecked,
            colors = CheckboxDefaults.colors(checkedColor = accent)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onOpen)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (!item.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(accent)
                    )
                }
                Text(
                    text = titleOf(item.type),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (item.isRead) FontWeight.Normal else FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = item.text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Удалить",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

private fun titleOf(type: String): String = when (type) {
    NotificationKinds.PROFILE -> "Профиль"
    "match" -> "Мэтч"
    else -> "Уведомление"
}
