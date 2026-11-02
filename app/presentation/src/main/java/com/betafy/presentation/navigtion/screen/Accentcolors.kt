package com.betafy.presentation.navigation.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.betafy.betafyapp.core.uikit.theme.extendedColors
import com.betafy.presentation.navigation.model.AccentRole

data class AccentColors(
    val accent: Color,
    val container: Color,
    val onContainer: Color
)

@Composable
fun AccentRole.colors(): AccentColors {
    val scheme = MaterialTheme.colorScheme
    val extended = MaterialTheme.extendedColors
    return when (this) {
        AccentRole.Lavender -> AccentColors(
            accent = scheme.secondary,
            container = scheme.secondaryContainer,
            onContainer = scheme.onSecondaryContainer
        )
        AccentRole.Tangerine -> AccentColors(
            accent = scheme.tertiary,
            container = scheme.tertiaryContainer,
            onContainer = scheme.onTertiaryContainer
        )
        AccentRole.Mint -> AccentColors(
            accent = extended.success.color,
            container = extended.success.colorContainer,
            onContainer = extended.success.onColorContainer
        )
        AccentRole.Blush -> AccentColors(
            accent = extended.blush.color,
            container = extended.blush.colorContainer,
            onContainer = extended.blush.onColorContainer
        )
    }
}