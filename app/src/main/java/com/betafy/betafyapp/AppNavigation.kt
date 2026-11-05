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
import com.betafy.feature.profile.presentation.ProfileCabinetViewModel
import com.betafy.feature.profile.presentation.ProfileEditScreen
import com.betafy.feature.profile.presentation.ProfileKind
import com.betafy.feature.profile.presentation.ProfileRoutes
import com.betafy.feature.splash.presentation.SplashScreen
import com.betafy.presentation.navigation.screen.NavigationScreen
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue

private const val SPLASH = "splash"
private const val LOGIN = "login"
private const val REGISTER = "register"
private const val HOME = "home"

private const val REGISTERED_KEY = "registered"
private const val PROFILE_NOTICE_KEY = "profile_notice"

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

        composable(HOME) { entry ->
            val profileNotice by entry.savedStateHandle
                .getStateFlow(PROFILE_NOTICE_KEY, "")
                .collectAsStateWithLifecycle()
            val cabinet: ProfileCabinetViewModel = hiltViewModel()
            val cabinetState by cabinet.state.collectAsStateWithLifecycle()
            LaunchedEffect(profileNotice) {
                cabinet.refresh(session.currentUserId())
            }

            NavigationScreen(
                profileNotice = profileNotice,
                readerCreateEnabled = cabinetState.checked && !cabinetState.readerExists,
                writerCreateEnabled = cabinetState.checked && !cabinetState.writerExists,
                onLogout = {
                    session.signOut()

                    navController.navigate(LOGIN) {
                        popUpTo(HOME) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },
                onCreateReader = {
                    entry.savedStateHandle[PROFILE_NOTICE_KEY] = ""
                    navController.navigate(ProfileRoutes.READER)
                },
                onCreateWriter = {
                    entry.savedStateHandle[PROFILE_NOTICE_KEY] = ""
                    navController.navigate(ProfileRoutes.WRITER)
                }
            )
        }

        composable(ProfileRoutes.READER) {
            ProfileEditScreen(
                kind = ProfileKind.READER,
                userId = session.currentUserId(),
                onBack = { navController.popBackStack() },
                onSaved = { message ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(PROFILE_NOTICE_KEY, message)
                    navController.popBackStack()
                }
            )
        }

        composable(ProfileRoutes.WRITER) {
            ProfileEditScreen(
                kind = ProfileKind.WRITER,
                userId = session.currentUserId(),
                onBack = { navController.popBackStack() },
                onSaved = { message ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(PROFILE_NOTICE_KEY, message)
                    navController.popBackStack()
                }
            )
        }
    }
}