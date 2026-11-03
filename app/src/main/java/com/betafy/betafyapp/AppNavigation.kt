package com.betafy.betafyapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.betafy.feature.auth.presentation.LoginScreen
import com.betafy.feature.auth.presentation.RegisterScreen
import com.betafy.feature.auth.presentation.SessionViewModel
import com.betafy.feature.splash.presentation.SplashScreen
import com.betafy.presentation.navigation.screen.NavigationScreen
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue

private const val SPLASH = "splash"
private const val LOGIN = "login"
private const val REGISTER = "register"
private const val HOME = "home"

private const val REGISTERED_KEY = "registered"

private const val SPLASH_DURATION = 5000L

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val session: SessionViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = SPLASH
    ) {

        // ====================================================
        // SPLASH SCREEN
        // ====================================================

        composable(SPLASH) {

            LaunchedEffect(Unit) {
                // Ждём завершения анимации заставки.
                delay(SPLASH_DURATION)

                // Проверяем авторизацию после заставки.
                val destination = if (session.isLoggedIn()) {
                    HOME
                } else {
                    LOGIN
                }

                navController.navigate(destination) {
                    popUpTo(SPLASH) {
                        inclusive = true
                    }

                    launchSingleTop = true
                }
            }

            SplashScreen()
        }

        // ====================================================
        // LOGIN
        // ====================================================

        composable(LOGIN) { entry ->

            val registered by entry.savedStateHandle
                .getStateFlow(REGISTERED_KEY, false)
                .collectAsStateWithLifecycle()

            LoginScreen(
                onAuthorized = {
                    navController.navigate(HOME) {
                        popUpTo(LOGIN) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },

                onRegisterClick = {
                    navController.navigate(REGISTER)
                },

                registered = registered
            )
        }

        // ====================================================
        // REGISTER
        // ====================================================

        composable(REGISTER) {

            RegisterScreen(
                onRegistered = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(REGISTERED_KEY, true)

                    navController.popBackStack()
                },

                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // ====================================================
        // HOME
        // ====================================================

        composable(HOME) {

            NavigationScreen(
                onLogout = {
                    session.signOut()

                    navController.navigate(LOGIN) {
                        popUpTo(HOME) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }
    }
}