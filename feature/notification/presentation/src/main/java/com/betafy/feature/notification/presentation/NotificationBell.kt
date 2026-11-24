package com.betafy.feature.notification.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.betafy.feature.notification.domain.model.InboxNotification
import com.betafy.feature.notification.domain.unreadBadge

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
                    .offset(x = 6.dp, y = (-4).dp)
                    .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                    .clip(CircleShape)
                    .background(accent)
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = onAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }

    if (opened) {
        NotificationListDialog(
            accent = accent,
            state = state,
            onDismiss = { opened = false },
            onMarkRead = viewModel::markRead,
            onDelete = viewModel::delete
        )
    }
}

@Composable
private fun NotificationListDialog(
    accent: Color,
    state: NotificationUiState,
    onDismiss: () -> Unit,
    onMarkRead: (List<String>) -> Unit,
    onDelete: (List<String>) -> Unit
) {
    var selected by remember { mutableStateOf(setOf<String>()) }
    var reading by remember { mutableStateOf<InboxNotification?>(null) }
    val ids = state.items.map { it.id }.toSet()
    val chosen = selected.intersect(ids)
    val markLabel = if (chosen.isEmpty()) "Прочитать все" else "Прочитать выбранные"
    val deleteLabel = if (chosen.isEmpty()) "Удалить все" else "Удалить выбранные"

    Dialog(
        onDismissRequest = onDismiss,
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
                            selected = emptySet()
                        },
                        enabled = state.items.isNotEmpty()
                    ) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = markLabel)
                    }
                    IconButton(
                        onClick = {
                            val target = if (chosen.isEmpty()) state.items.map { it.id } else chosen.toList()
                            onDelete(target)
                            selected = emptySet()
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
                                    onChecked = { on ->
                                        selected = if (on) selected + item.id else selected - item.id
                                    },
                                    onOpen = { reading = item },
                                    onDelete = { onDelete(listOf(item.id)) }
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                            }
                        }
                    }
                }
            }
        }
    }

    reading?.let { item ->
        NotificationDetailDialog(
            item = item,
            onDismiss = { reading = null },
            onMarkRead = onMarkRead,
            onDelete = {
                onDelete(listOf(item.id))
                reading = null
            }
        )
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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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

@Composable
private fun NotificationDetailDialog(
    item: InboxNotification,
    onDismiss: () -> Unit,
    onMarkRead: (List<String>) -> Unit,
    onDelete: () -> Unit
) {
    LaunchedEffect(item.id) {
        if (!item.isRead) onMarkRead(listOf(item.id))
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titleOf(item.type)) },
        text = { Text(item.text) },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Закрыть") }
        },
        dismissButton = {
            TextButton(onClick = onDelete) { Text("Удалить") }
        }
    )
}

private fun titleOf(type: String): String = when (type) {
    NotificationKinds.PROFILE -> "Профиль"
    "match" -> "Мэтч"
    else -> "Уведомление"
}
