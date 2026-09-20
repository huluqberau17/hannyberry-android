package com.hannyberry.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun HannyBerryTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(
            primary = Berry,
            onPrimary = Color.White,
            primaryContainer = SoftPink,
            onPrimaryContainer = BerryDark,
            secondary = Leaf,
            onSecondary = Color.White,
            background = Cream,
            onBackground = Ink,
            surface = Color.White,
            onSurface = Ink,
            error = Color(0xFFB3261E),
            onError = Color.White,
        ),
        typography = Typography,
        content = content
    )
}
