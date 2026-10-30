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

private val ZBrowserColorScheme = darkColorScheme(
    primary = ZCyanElectric,
    onPrimary = ZSpaceNavyDark,
    primaryContainer = ZSurfaceElevated,
    onPrimaryContainer = ZCyanElectric,
    secondary = ZOrangeRadiant,
    onSecondary = ZSpaceNavyDark,
    secondaryContainer = ZSurfaceCard,
    onSecondaryContainer = ZOrangeSunburst,
    tertiary = ZGlobeBlue,
    background = ZSpaceNavy,
    onBackground = ZTextPrimary,
    surface = ZSurfaceNavy,
    onSurface = ZTextPrimary,
    surfaceVariant = ZSurfaceElevated,
    onSurfaceVariant = ZTextSecondary,
    outline = ZBorderGlow
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Z Browser defaults to its signature cyber dark theme
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = ZSpaceNavy.toArgb()
                window.navigationBarColor = ZSpaceNavy.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = ZBrowserColorScheme,
        typography = Typography,
        content = content
    )
}
