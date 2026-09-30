package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CinePulseColorScheme = darkColorScheme(
    primary = CineRed,
    onPrimary = Color.White,
    primaryContainer = CineRedDark,
    onPrimaryContainer = Color(0xFFFFD8DC),
    secondary = CineCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF00363F),
    onSecondaryContainer = Color(0xFFB8F5FF),
    tertiary = CineGold,
    onTertiary = Color.Black,
    background = CineBackground,
    onBackground = CineTextPrimary,
    surface = CineSurface,
    onSurface = CineTextPrimary,
    surfaceVariant = CineSurfaceVariant,
    onSurfaceVariant = CineTextSecondary,
    outline = CineCardBorder,
    error = CineRedLight,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CinePulseColorScheme,
        typography = Typography,
        content = content
    )
}
