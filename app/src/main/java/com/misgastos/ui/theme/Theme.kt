package com.misgastos.ui.theme

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
    primary = IceBlue,
    onPrimary = NightInk,
    primaryContainer = Color(0xFF243246),
    onPrimaryContainer = Color(0xFFDCE8FF),
    secondary = SlateAccent,
    onSecondary = NightInk,
    tertiary = WarmSand,
    onTertiary = NightInk,
    background = NightInk,
    onBackground = Color(0xFFE7EDF6),
    surface = NightSurface,
    onSurface = Color(0xFFE7EDF6),
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = Color(0xFFC4CFDC),
    outline = Color(0xFF8793A2),
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
