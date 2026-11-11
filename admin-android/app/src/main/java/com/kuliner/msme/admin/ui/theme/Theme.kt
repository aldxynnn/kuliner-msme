package com.kuliner.msme.admin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PosPrimary,
    onPrimary = PosSurface,
    primaryContainer = PosSoftAccent,
    onPrimaryContainer = PosPrimary,
    secondary = PosPrimaryAccent,
    background = PosBg,
    surface = PosSurface,
    onBackground = PosMainText,
    onSurface = PosMainText,
    error = PosError
)

@Composable
fun KulinerAdminTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
