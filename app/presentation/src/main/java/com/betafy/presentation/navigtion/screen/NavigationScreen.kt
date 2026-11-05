package com.betafy.presentation.navigation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.betafy.betafyapp.core.uikit.theme.extendedColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.betafy.presentation.navigation.model.NavigationBottomModel

@Composable
fun NavigationScreen(
    onLogout: () -> Unit,
    onCreateReader: () -> Unit = {},
    onCreateWriter: () -> Unit = {},
    profileNotice: String = "",
    readerCreateEnabled: Boolean = true,
    writerCreateEnabled: Boolean = true,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentItem = NavigationBottomModel.getNavItems()
        .firstOrNull { it.route == currentRoute } ?: NavigationBottomModel.Search

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { AppBottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScreenHeader(item = currentItem)

            if (profileNotice.isNotBlank()) {
                val success = MaterialTheme.extendedColors.success
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = MaterialTheme.shapes.small,
                    color = success.colorContainer,
                    contentColor = success.onColorContainer
                ) {
                    Text(
                        text = profileNotice,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }

            NavHost(
                navController = navController,
                startDestination = NavigationBottomModel.Search.route,
                modifier = Modifier.weight(1f)
            ) {
                composable(NavigationBottomModel.Search.route) { PlaceholderScreen("Поиск") }
                composable(NavigationBottomModel.Match.route) { PlaceholderScreen("Мэтч") }
                composable(NavigationBottomModel.Chats.route) { PlaceholderScreen("Чаты") }
                composable(NavigationBottomModel.Profile.route) {
                    ProfilePlaceholder(
                        onLogout = onLogout,
                        onCreateReader = onCreateReader,
                        onCreateWriter = onCreateWriter,
                        readerCreateEnabled = readerCreateEnabled,
                        writerCreateEnabled = writerCreateEnabled
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun ProfilePlaceholder(
    onLogout: () -> Unit,
    onCreateReader: () -> Unit = {},
    onCreateWriter: () -> Unit = {},
    readerCreateEnabled: Boolean = true,
    writerCreateEnabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val gapPx = with(LocalDensity.current) { 12.dp.roundToPx() }
    val positionProvider = remember(gapPx) { AboveAnchorPositionProvider(gapPx) }

    BackHandler(enabled = expanded) { expanded = false }

    Box(modifier = Modifier.fillMaxSize()) {

        // Слой 1: основной контент
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Профиль", color = MaterialTheme.colorScheme.onSurface)

            Spacer(Modifier.height(16.dp))

            // Якорь: новые кнопки появляются прямо над этой кнопкой
            Box {
                Button(onClick = { expanded = true }) {
                    Text("Создать аккаунт")
                }

                if (expanded) {
                    Popup(popupPositionProvider = positionProvider) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    expanded = false
                                    onCreateReader()
                                },
                                enabled = readerCreateEnabled
                            ) {
                                Text(if (readerCreateEnabled) "Добавить профиль ридера" else "Профиль ридера уже создан")
                            }

                            Button(
                                onClick = {
                                    expanded = false
                                    onCreateWriter()
                                },
                                enabled = writerCreateEnabled
                            ) {
                                Text(if (writerCreateEnabled) "Добавить профиль писателя" else "Профиль писателя уже создан")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = onLogout) { Text("Выйти") }
        }

        // Слой 2: затемнение (перекрывает контент и кнопки слоя 1)
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { expanded = false }
            )
        }
    }
}

/** Располагает popup по центру над якорной кнопкой с отступом [gapPx]. */
private class AboveAnchorPositionProvider(
    private val gapPx: Int
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val x = (anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2)
            .coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0))
        val y = (anchorBounds.top - popupContentSize.height - gapPx).coerceAtLeast(0)
        return IntOffset(x, y)
    }
}