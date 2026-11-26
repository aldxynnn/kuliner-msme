package com.kuliner.msme.admin.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AromaTerracotta,
    onPrimary = AromaSurface,
    primaryContainer = AromaTerracottaSoft,
    onPrimaryContainer = AromaTerracottaDark,
    secondary = AromaCharcoal,
    onSecondary = AromaSurface,
    secondaryContainer = AromaCreamTrack,
    onSecondaryContainer = AromaCharcoal,
    tertiary = AromaSuccess,
    onTertiary = AromaSurface,
    background = AromaCream,
    onBackground = AromaCharcoal,
    surface = AromaSurface,
    onSurface = AromaCharcoal,
    surfaceVariant = AromaCreamSoft,
    onSurfaceVariant = AromaTextSecondary,
    outline = AromaBorder,
    outlineVariant = AromaDivider,
    error = AromaError,
    onError = AromaSurface,
    errorContainer = AromaErrorSoft,
    onErrorContainer = AromaError
)

@Composable
fun KulinerAdminTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                @Suppress("DEPRECATION")
                window.statusBarColor = AromaCream.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
