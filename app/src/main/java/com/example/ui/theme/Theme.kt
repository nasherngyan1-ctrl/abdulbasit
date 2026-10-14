package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LuxuryDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = MatteBlack,
    primaryContainer = GoldMuted,
    onPrimaryContainer = GoldLight,
    secondary = GoldLight,
    onSecondary = MatteBlack,
    secondaryContainer = MatteBlackElevated,
    onSecondaryContainer = GoldLight,
    tertiary = GoldMuted,
    onTertiary = TextWhite,
    background = MatteBlack,
    onBackground = TextWhite,
    surface = MatteBlackSurface,
    onSurface = TextWhite,
    surfaceVariant = MatteBlackElevated,
    onSurfaceVariant = TextMuted,
    outline = DarkCardBorder,
    outlineVariant = MatteBlackElevated
)

@Composable
fun AbdulbasitTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LuxuryDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
