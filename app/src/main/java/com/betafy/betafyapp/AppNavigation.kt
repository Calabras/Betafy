package com.betafy.betafyapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.betafy.feature.auth.presentation.LoginScreen
import com.betafy.feature.auth.presentation.RegisterScreen
import com.betafy.feature.auth.presentation.SessionViewModel
import com.betafy.presentation.navigation.screen.NavigationScreen

private const val LOGIN = "login"
private const val REGISTER = "register"
private const val HOME = "home"
private const val REGISTERED_KEY = "registered"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val session: SessionViewModel = hiltViewModel()
    val start = if (session.isLoggedIn()) HOME else LOGIN

    NavHost(navController = navController, startDestination = start) {
        composable(LOGIN) { entry ->
            val registered by entry.savedStateHandle
                .getStateFlow(REGISTERED_KEY, false)
                .collectAsStateWithLifecycle()
            LoginScreen(
                onAuthorized = {
                    navController.navigate(HOME) { popUpTo(LOGIN) { inclusive = true } }
                },
                onRegisterClick = { navController.navigate(REGISTER) },
                registered = registered
            )
        }
        composable(REGISTER) {
            RegisterScreen(
                onRegistered = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(REGISTERED_KEY, true)
                    navController.popBackStack()
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable(HOME) {
            NavigationScreen(
                onLogout = {
                    session.signOut()
                    navController.navigate(LOGIN) { popUpTo(HOME) { inclusive = true } }
                }
            )
        }
    }
}