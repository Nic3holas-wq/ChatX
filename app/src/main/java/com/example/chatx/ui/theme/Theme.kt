package com.example.chatx.ui.theme

import android.app.Activity
import android.graphics.Color
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorPalette = darkColorScheme(
    primary = darkPrimary,
    background = darkBackground,
    onPrimary = darkOnPrimary,
    onBackground = darkOnBackground

)

private val LightColorPalette = lightColorScheme(
    primary = lightPrimary,
    background = lightBackground,
    onPrimary = lightOnPrimary,
    onBackground = lightOnBackground
)

@Composable
fun ChatXTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (isDarkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colorScheme = colors ,
        typography = Typography,
        content = content
    )
}