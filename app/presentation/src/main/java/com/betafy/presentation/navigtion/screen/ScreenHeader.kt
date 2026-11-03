package com.betafy.presentation.navigation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.betafy.betafyapp.core.uikit.theme.BetafyHeaderText
import com.betafy.presentation.navigation.model.AccentRole
import com.betafy.presentation.navigation.model.NavigationBottomModel

@Composable
fun ScreenHeader(
    item: NavigationBottomModel,
    modifier: Modifier = Modifier
) {
    val accentColors = AccentRole.entries.associateWith { it.colors().accent }
    val textColor = MaterialTheme.colorScheme.onSurface
    val captionColor = MaterialTheme.colorScheme.onSurfaceVariant
    val buttonColor = MaterialTheme.colorScheme.surfaceContainerHigh

    AnimatedContent(
        targetState = item,
        modifier = modifier.fillMaxWidth(),
        transitionSpec = {
            (fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 4 })
                .togetherWith(fadeOut(tween(150)))
        },
        label = "header"
    ) { target ->

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(Modifier.weight(1f)) {

                Text(
                    text = target.caption,
                    style = BetafyHeaderText.caption,
                    color = captionColor
                )

                target.headerLines.forEach { line ->

                    Text(
                        text = buildAnnotatedString {
                            line.forEach { part ->
                                val color = part.accent
                                    ?.let { accentColors.getValue(it) }
                                    ?: textColor

                                withStyle(SpanStyle(color = color)) {
                                    append(part.text)
                                }
                            }
                        },
                        style = BetafyHeaderText.title
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(buttonColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = textColor
                )
            }
        }
    }
}