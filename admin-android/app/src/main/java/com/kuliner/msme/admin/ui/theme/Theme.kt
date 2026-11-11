package com.kuliner.msme.admin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = TerracottaLight,
    onPrimaryContainer = TerracottaDark,
    secondary = TerracottaDark,
    background = WarmIvoryBg,
    surface = SurfaceWhite,
    onBackground = DarkCharcoal,
    onSurface = DarkCharcoal,
    error = ErrorRed
)

@Composable
fun KulinerAdminTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
