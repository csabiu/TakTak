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
    primary = Color(0xFF4D3D30),           // Dark brown from icon jar
    onPrimary = Color(0xFFFFF8F0),         // Off-white for contrast
    primaryContainer = Color(0xFFE8DDD0),  // Light beige from icon background
    onPrimaryContainer = Color(0xFF2B1E15),
    secondary = Color(0xFF8B7355),         // Medium tan/brown
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD4C4B0),
    onSecondaryContainer = Color(0xFF3A2E20),
    tertiary = Color(0xFFA67C52),          // Warm tan
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF5E6D3),
    onTertiaryContainer = Color(0xFF3D2E1F),
    background = Color(0xFFFFF8F0),        // Warm off-white
    onBackground = Color(0xFF2B1E15),
    surface = Color(0xFFFFFBF5),           // Slightly warmer surface
    onSurface = Color(0xFF2B1E15),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD4C4B0),           // Light tan/beige
    onPrimary = Color(0xFF2B1E15),         // Dark brown
    primaryContainer = Color(0xFF5A4838),  // Medium dark brown
    onPrimaryContainer = Color(0xFFE8DDD0),
    secondary = Color(0xFFCBB299),         // Warm tan
    onSecondary = Color(0xFF3A2E20),
    secondaryContainer = Color(0xFF4A3C2E),
    onSecondaryContainer = Color(0xFFE8DDD0),
    tertiary = Color(0xFFB89968),          // Golden tan
    onTertiary = Color(0xFF2F2419),
    tertiaryContainer = Color(0xFF5D4A35),
    onTertiaryContainer = Color(0xFFF5E6D3),
    background = Color(0xFF1F1912),        // Dark warm brown
    onBackground = Color(0xFFE8DDD0),
    surface = Color(0xFF27211A),           // Slightly lighter dark brown
    onSurface = Color(0xFFE8DDD0),
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
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
