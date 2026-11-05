package com.betafy.feature.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.betafy.betafyapp.core.uikit.components.BetafyLogo
import com.betafy.betafyapp.core.uikit.theme.extendedColors

enum class AuthTab {
    Login,
    Register
}

// ====================================================
// ФИКСИРОВАННЫЕ ЦВЕТА ПЛАШЕК (не зависят от темы)
// ====================================================

private data class StickerColors(
    val background: Color,
    val text: Color
)

private object HeroStickerPalette {
    val plot = StickerColors(
        background = Color(0xFFFFD9E0),
        text = Color(0xFF8A2D45)
    )
    val style = StickerColors(
        background = Color(0xFFD6E6FF),
        text = Color(0xFF1D4F91)
    )
    val heroes = StickerColors(
        background = Color(0xFFFFF0B3),
        text = Color(0xFF7A5A00)
    )
    val draft = StickerColors(
        background = Color(0xFFE5DBFF),
        text = Color(0xFF4E3886)
    )
    val reader = StickerColors(
        background = Color(0xFFCDEFD8),
        text = Color(0xFF1F6B3A)
    )
    val stories = StickerColors(
        background = Color(0xFFFFD6B8),
        text = Color(0xFF8B370C)
    )
    val dialogue = StickerColors(
        background = Color(0xFFFFD9E0),
        text = Color(0xFF8A2D45)
    )
}

// ====================================================
// HERO: ЗЕЛЁНАЯ КАРТОЧКА И ДЕКОРАТИВНЫЕ ПЛАШКИ
// ====================================================

@Composable
fun AuthHero(
    modifier: Modifier = Modifier,
    height: Dp = 180.dp
) {
    val colors = MaterialTheme.colorScheme

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val heroWidth = maxWidth
        val heroHeight = maxHeight

        // Зелёная карточка — 75% доступной ширины.
        val greenWidth = heroWidth * 0.75f

        val stickerHeight = 36.dp
        val stickerFontSize = (greenWidth.value * 0.049f).sp
        val stickerBorderWidth = greenWidth * 0.004f
        val stickerPadding = greenWidth * 0.048f

        // ------------------------------------------------
        // ЗЕЛЁНАЯ КАРТОЧКА
        // ------------------------------------------------

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(greenWidth)
                .fillMaxHeight(0.78f)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(colors.primary),
            contentAlignment = Alignment.Center
        ) {
            BetafyLogo(
                modifier = Modifier.height(heroHeight * 0.40f),
                color = colors.background,
                showWordmark = true
            )
        }

        // ------------------------------------------------
        // СЮЖЕТ
        // ------------------------------------------------

        HeroSticker(
            text = "сюжет",
            backgroundColor = HeroStickerPalette.plot.background,
            textColor = HeroStickerPalette.plot.text,
            rotation = -12f,
            height = stickerHeight,
            fontSize = stickerFontSize,
            borderWidth = stickerBorderWidth,
            horizontalPadding = stickerPadding,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    x = heroWidth * 0.10f,
                    y = heroHeight * 0.025f
                )
        )

        // ------------------------------------------------
        // СТИЛЬ
        // ------------------------------------------------

        HeroSticker(
            text = "стиль",
            backgroundColor = HeroStickerPalette.style.background,
            textColor = HeroStickerPalette.style.text,
            rotation = 3f,
            height = stickerHeight,
            fontSize = stickerFontSize,
            borderWidth = stickerBorderWidth,
            horizontalPadding = stickerPadding,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = heroHeight * 0.02f)
        )

        // ------------------------------------------------
        // ГЕРОИ
        // ------------------------------------------------

        HeroSticker(
            text = "герои",
            backgroundColor = HeroStickerPalette.heroes.background,
            textColor = HeroStickerPalette.heroes.text,
            rotation = 12f,
            height = stickerHeight,
            fontSize = stickerFontSize,
            borderWidth = stickerBorderWidth,
            horizontalPadding = stickerPadding,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(
                    x = -(heroWidth * 0.10f),
                    y = heroHeight * 0.025f
                )
        )

        // ------------------------------------------------
        // ЧЕРНОВИК
        // ------------------------------------------------

        HeroSticker(
            text = "черновик",
            backgroundColor = HeroStickerPalette.draft.background,
            textColor = HeroStickerPalette.draft.text,
            rotation = 15f,
            height = stickerHeight,
            fontSize = stickerFontSize,
            borderWidth = stickerBorderWidth,
            horizontalPadding = stickerPadding,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(
                    x = heroWidth * 0.01f,
                    y = -(heroHeight * 0.01f)
                )
        )

        // ------------------------------------------------
        // БЕТА-РИДЕР
        // ------------------------------------------------

        HeroSticker(
            text = "бета-ридер",
            backgroundColor = HeroStickerPalette.reader.background,
            textColor = HeroStickerPalette.reader.text,
            rotation = -15f,
            height = stickerHeight,
            fontSize = stickerFontSize,
            borderWidth = stickerBorderWidth,
            horizontalPadding = stickerPadding,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(
                    x = -(heroWidth * 0.01f),
                    y = heroHeight * 0.02f
                )
        )

        // ------------------------------------------------
        // ИСТОРИИ
        // ------------------------------------------------

        HeroSticker(
            text = "истории",
            backgroundColor = HeroStickerPalette.stories.background,
            textColor = HeroStickerPalette.stories.text,
            rotation = -12f,
            height = stickerHeight,
            fontSize = stickerFontSize,
            borderWidth = stickerBorderWidth,
            horizontalPadding = stickerPadding,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(
                    x = heroWidth * 0.16f,
                    y = -(heroHeight * 0.005f)
                )
        )

        // ------------------------------------------------
        // ДИАЛОГИ
        // ------------------------------------------------

        HeroSticker(
            text = "диалоги",
            backgroundColor = HeroStickerPalette.dialogue.background,
            textColor = HeroStickerPalette.dialogue.text,
            rotation = 10f,
            height = stickerHeight,
            fontSize = stickerFontSize,
            borderWidth = stickerBorderWidth,
            horizontalPadding = stickerPadding,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(
                    x = -(heroWidth * 0.15f),
                    y = -(heroHeight * 0.005f)
                )
        )
    }
}

// ====================================================
// ДЕКОРАТИВНАЯ ПЛАШКА
// ====================================================

@Composable
private fun HeroSticker(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    rotation: Float,
    height: Dp,
    fontSize: TextUnit,
    borderWidth: Dp,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .rotate(rotation)
            .height(height)
            .clip(CircleShape)
            .background(
                color = backgroundColor.copy(alpha = 1f)
            )
            .border(
                width = borderWidth,
                color = textColor.copy(alpha = 1f),
                shape = CircleShape
            )
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = fontSize
            ),
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.Center
        )
    }
}

// ====================================================
// ЗАГОЛОВОК И ПОДЗАГОЛОВОК
// ====================================================

@Composable
fun AuthHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ====================================================
// ПЕРЕКЛЮЧАТЕЛЬ ВХОД / РЕГИСТРАЦИЯ
// ====================================================

@Composable
fun AuthTabSwitch(
    selected: AuthTab,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium
            )
            .padding(4.dp)
    ) {
        AuthTabItem(
            text = "Вход",
            selected = selected == AuthTab.Login,
            onClick = onLoginClick,
            modifier = Modifier.weight(1f)
        )

        AuthTabItem(
            text = "Регистрация",
            selected = selected == AuthTab.Register,
            onClick = onRegisterClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AuthTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    val container = if (selected) {
        colors.primary
    } else {
        Color.Transparent
    }

    val content = if (selected) {
        colors.onPrimary
    } else {
        colors.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(MaterialTheme.shapes.small)
            .background(container)
            .selectable(
                selected = selected,
                role = Role.Tab,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = content
        )
    }
}

// ====================================================
// ПОЛЯ ВВОДА
// ====================================================

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    val colors = MaterialTheme.colorScheme

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null
            )
        },
        trailingIcon = if (isPassword) {
            {
                PasswordToggle(
                    visible = passwordVisible,
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                )
            }
        } else {
            null
        },
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surfaceContainerLow,
            unfocusedContainerColor = colors.surfaceContainerLow,
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.outlineVariant,
            focusedLabelColor = colors.primary,
            focusedLeadingIconColor = colors.primary,
            unfocusedLeadingIconColor = colors.onSurfaceVariant,
            cursorColor = colors.primary
        )
    )
}

// ====================================================
// ПОКАЗАТЬ / СКРЫТЬ ПАРОЛЬ
// ====================================================

@Composable
private fun PasswordToggle(
    visible: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = if (visible) "Скрыть" else "Показать",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            )
    )
}

// ====================================================
// СООБЩЕНИЕ ОБ ОШИБКЕ
// ====================================================

@Composable
fun AuthErrorMessage(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    ) {
        Text(
            text = notificationText(text),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
        )
    }
}

// ====================================================
// СООБЩЕНИЕ ОБ УСПЕХЕ
// ====================================================

@Composable
fun AuthSuccessMessage(
    text: String,
    modifier: Modifier = Modifier
) {
    val success = MaterialTheme.extendedColors.success

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = success.colorContainer,
        contentColor = success.onColorContainer
    ) {
        Text(
            text = notificationText(text),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
        )
    }
}

private fun notificationText(text: String): String =
    text.replace(". ", ", ").trimEnd('.', ' ')