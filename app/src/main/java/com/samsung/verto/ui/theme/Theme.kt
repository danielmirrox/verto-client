package com.samsung.verto.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PulseLime,
    onPrimary = PulseInk,
    secondary = PulseCyan,
    onSecondary = PulseInk,
    tertiary = PulseCoral,
    onTertiary = PulseInk,
    background = PulseInk,
    onBackground = PulseFog,
    surface = PulseSurface,
    onSurface = PulseFog,
    surfaceVariant = PulseSurfaceSoft,
    onSurfaceVariant = PulseMuted,
    error = PulseCoral,
    onError = PulseInk,
)

private val LightColorScheme = lightColorScheme(
    primary = PulseLime,
    onPrimary = PulseInk,
    secondary = PulseCyan,
    onSecondary = PulseInk,
    tertiary = PulseCoral,
    onTertiary = PulseInk,
    background = PulseFog,
    onBackground = PulseInk,
    surface = Color(0xFFFFFFFF),
    onSurface = PulseInk,
    surfaceVariant = Color(0xFFE7EBF0),
    onSurfaceVariant = PulseMuted,
    error = PulseCoral,
    onError = PulseInk,
)

@Composable
fun VertoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}