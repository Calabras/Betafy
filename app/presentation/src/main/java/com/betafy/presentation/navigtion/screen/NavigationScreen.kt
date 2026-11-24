package com.betafy.presentation.navigation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.betafy.betafyapp.core.uikit.theme.BetafySizes
import com.betafy.betafyapp.core.uikit.theme.extendedColors
import kotlinx.coroutines.launch
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
    profilesChecked: Boolean = true,
    readerExists: Boolean = false,
    writerExists: Boolean = false,
    bell: (@Composable (NavigationBottomModel) -> Unit)? = null,
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
            ScreenHeader(
                item = currentItem,
                bell = bell?.let { slot -> { slot(currentItem) } }
            )

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
                        profilesChecked = profilesChecked,
                        readerExists = readerExists,
                        writerExists = writerExists
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
    profilesChecked: Boolean = true,
    readerExists: Boolean = false,
    writerExists: Boolean = false
) {
    var confirmLogout by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    val success = MaterialTheme.extendedColors.success

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Профиль", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(20.dp))
        RoleSwitch(
            selected = pagerState.currentPage,
            onSelect = { index -> scope.launch { pagerState.animateScrollToPage(index) } }
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            val exists = if (page == 0) readerExists else writerExists
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { if (page == 0) onCreateReader() else onCreateWriter() },
                    enabled = profilesChecked,
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = success.color,
                        contentColor = success.onColor
                    )
                ) {
                    Text(
                        text = if (exists) "Посмотреть профиль" else "Создать профиль",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
        TextButton(
            onClick = { confirmLogout = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Выйти", color = MaterialTheme.colorScheme.error)
        }
    }

    if (confirmLogout) {
        LogoutDialog(
            onYes = {
                confirmLogout = false
                onLogout()
            },
            onNo = { confirmLogout = false }
        )
    }
}

@Composable
private fun RoleSwitch(
    selected: Int,
    onSelect: (Int) -> Unit
) {
    val labels = listOf("Ридер", "Писатель")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(4.dp)
    ) {
        labels.forEachIndexed { index, label ->
            val active = selected == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (active) MaterialTheme.colorScheme.onSurface else Color.Transparent
                    )
                    .clickable { onSelect(index) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (active) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun LogoutDialog(
    onYes: () -> Unit,
    onNo: () -> Unit
) {
    Dialog(
        onDismissRequest = onNo,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Выход", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Вы хотите выйти из аккаунта?",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = onYes,
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
                    )
                ) {
                    Text(text = "Да")
                }
                TextButton(
                    onClick = onNo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BetafySizes.buttonHeight),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Нет")
                }
            }
        }
    }
}