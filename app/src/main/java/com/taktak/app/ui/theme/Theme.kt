package com.taktak.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6A4C93),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8DEF8),
    onPrimaryContainer = Color(0xFF21005E),
    secondary = Color(0xFF8B7355),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDDB3),
    onSecondaryContainer = Color(0xFF2B1700),
    tertiary = Color(0xFF00677F),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB8EAFF),
    onTertiaryContainer = Color(0xFF001F28),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1C1B1E),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF1C1B1E),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378A),
    onPrimaryContainer = Color(0xFFE8DEF8),
    secondary = Color(0xFFFFDDB3),
    onSecondary = Color(0xFF462B00),
    secondaryContainer = Color(0xFF664000),
    onSecondaryContainer = Color(0xFFFFDDB3),
    tertiary = Color(0xFF5FD4FF),
    onTertiary = Color(0xFF003544),
    tertiaryContainer = Color(0xFF004D61),
    onTertiaryContainer = Color(0xFFB8EAFF),
    background = Color(0xFF1C1B1E),
    onBackground = Color(0xFFE6E1E6),
    surface = Color(0xFF1C1B1E),
    onSurface = Color(0xFFE6E1E6),
)

@Composable
fun TakTakTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
