package com.betafy.presentation.navigation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.betafy.presentation.navigation.model.NavigationBottomModel

@Composable
fun NavigationScreen(
    onLogout: () -> Unit,
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

            NavHost(
                navController = navController,
                startDestination = NavigationBottomModel.Search.route,
                modifier = Modifier.weight(1f)
            ) {
                composable(NavigationBottomModel.Search.route) { PlaceholderScreen("Поиск") }
                composable(NavigationBottomModel.Match.route) { PlaceholderScreen("Мэтч") }
                composable(NavigationBottomModel.Chats.route) { PlaceholderScreen("Чаты") }
                composable(NavigationBottomModel.Profile.route) { ProfilePlaceholder(onLogout) }
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
private fun ProfilePlaceholder(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Профиль", color = MaterialTheme.colorScheme.onSurface)
        Button(onClick = onLogout) { Text("Выйти") }
    }
}