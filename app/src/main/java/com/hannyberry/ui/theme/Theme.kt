package com.hannyberry.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Berry,
    onPrimary = Color.White,
    primaryContainer = SoftPink,
    onPrimaryContainer = BerryDark,
    secondary = Leaf,
    onSecondary = Color.White,
    secondaryContainer = SoftGreen,
    onSecondaryContainer = Ink,
    background = Cream,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    outline = Ink.copy(alpha = 0.35f),
    error = Color(0xFFB3261E),
    onError = Color.White,
)

@Composable
fun HannyBerryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content,
    )
}
