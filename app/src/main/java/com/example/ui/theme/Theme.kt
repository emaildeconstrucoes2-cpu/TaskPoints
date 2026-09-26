package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val KineticBloomColorScheme = darkColorScheme(
    primary = NeoEmerald,
    onPrimary = NeoEmeraldDark,
    primaryContainer = NeoEmeraldContainer,
    onPrimaryContainer = NeoEmeraldLight,
    secondary = HyperViolet,
    onSecondary = HyperVioletDark,
    secondaryContainer = HyperVioletDark,
    onSecondaryContainer = HyperVioletLight,
    tertiary = SolarAmber,
    onTertiary = SolarAmberDark,
    tertiaryContainer = SolarAmberDark,
    onTertiaryContainer = SolarAmberLight,
    background = SurfaceDark,
    onBackground = OnSurface,
    surface = SurfaceDark,
    onSurface = OnSurface,
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = OnSurfaceVariant,
    outline = OutlineColor,
    outlineVariant = OutlineVariant,
    error = ErrorColor
)

@Composable
fun TaskPointsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // The design is an intentional dark-mode OLED palette (Kinetic Bloom)
    val colorScheme = KineticBloomColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = SurfaceDark.toArgb()
                window.navigationBarColor = SurfaceDark.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
