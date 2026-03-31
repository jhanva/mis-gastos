package com.johan.misgastos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Emerald,
    onPrimary = Mist,
    primaryContainer = Color(0xFFC1EBDC),
    onPrimaryContainer = Charcoal,
    secondary = Stone,
    onSecondary = Mist,
    tertiary = Sand,
    onTertiary = Mist,
    background = Mist,
    onBackground = Charcoal,
    surface = Color(0xFFFFFFFF),
    onSurface = Charcoal,
    surfaceVariant = Color(0xFFE2ECE6),
    onSurfaceVariant = Color(0xFF404842),
    outline = Color(0xFF728178),
)

private val DarkColors = darkColorScheme(
    primary = MintAccent,
    onPrimary = ForestNight,
    primaryContainer = Color(0xFF114235),
    onPrimaryContainer = Color(0xFFC1EBDC),
    secondary = SlateAccent,
    onSecondary = ForestNight,
    tertiary = Color(0xFFF1CC94),
    onTertiary = ForestNight,
    background = ForestNight,
    onBackground = Color(0xFFEAF2EE),
    surface = Color(0xFF0D1714),
    onSurface = Color(0xFFEAF2EE),
    surfaceVariant = Color(0xFF20302B),
    onSurfaceVariant = Color(0xFFC1D0C9),
    outline = Color(0xFF8A9B92),
)

@Composable
fun MisGastosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
}
