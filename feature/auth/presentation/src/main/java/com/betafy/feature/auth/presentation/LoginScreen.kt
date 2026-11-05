package com.betafy.feature.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.betafy.betafyapp.core.uikit.theme.BetafySpacing
import com.betafy.feature.auth.presentation.components.AuthErrorMessage
import com.betafy.feature.auth.presentation.components.AuthHeader
import com.betafy.feature.auth.presentation.components.AuthHero
import com.betafy.feature.auth.presentation.components.AuthPrimaryButton
import com.betafy.feature.auth.presentation.components.AuthSuccessMessage
import com.betafy.feature.auth.presentation.components.AuthTab
import com.betafy.feature.auth.presentation.components.AuthTabSwitch
import com.betafy.feature.auth.presentation.components.AuthTextField

@Composable
fun LoginScreen(
    onAuthorized: () -> Unit,
    onRegisterClick: () -> Unit,
    registered: Boolean = false,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onAuthorized()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = BetafySpacing.screenHorizontal,
                vertical = BetafySpacing.lg
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthHero()

        Spacer(Modifier.height(BetafySpacing.lg))

        AuthHeader(
            title = "С возвращением!",
            subtitle = "Умный подбор интересных книг и подходящих бета-ридеров"
        )

        Spacer(Modifier.height(BetafySpacing.lg))

        AuthTabSwitch(
            selected = AuthTab.Login,
            onLoginClick = {},
            onRegisterClick = onRegisterClick
        )

        Spacer(Modifier.height(BetafySpacing.md))

        if (registered) {
            AuthSuccessMessage(text = "Аккаунт создан, теперь войдите")
            Spacer(Modifier.height(BetafySpacing.sm))
        }

        AuthTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = "Электронная почта",
            leadingIcon = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Modifier.height(BetafySpacing.sm))

        AuthTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Пароль",
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { viewModel.signIn() })
        )

        state.error?.let {
            Spacer(Modifier.height(BetafySpacing.sm))
            AuthErrorMessage(text = it)
        }

        Spacer(Modifier.height(BetafySpacing.lg))

        AuthPrimaryButton(
            text = "Войти",
            onClick = viewModel::signIn,
            isLoading = state.isLoading
        )
    }
}