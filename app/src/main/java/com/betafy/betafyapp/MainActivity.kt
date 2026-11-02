package com.betafy.betafyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.betafy.betafyapp.core.uikit.theme.BetafyTheme
import com.betafy.betafyapp.core.uikit.theme.BetafyContrast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BetafyTheme(
                darkTheme = isSystemInDarkTheme(),
                contrast = BetafyContrast.Standard,
                dynamicColor = false
            ) {
                AppNavigation()
            }
        }
    }
}
