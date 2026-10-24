package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ShaktiGold,
    onPrimary = ShaktiDark,
    primaryContainer = ShaktiDarkCard,
    onPrimaryContainer = ShaktiGoldLight,
    secondary = ShaktiRedLight,
    onSecondary = Color.White,
    secondaryContainer = ShaktiDarkSurface,
    onSecondaryContainer = ShaktiCream,
    tertiary = ShaktiGoldDark,
    onTertiary = ShaktiDark,
    background = ShaktiDark,
    onBackground = ShaktiCream,
    surface = ShaktiDarkSurface,
    onSurface = ShaktiCream,
    surfaceVariant = ShaktiDarkCard,
    onSurfaceVariant = ShaktiGoldLight,
    outline = ShaktiGold.copy(alpha = 0.5f),
)

private val LightColorScheme = lightColorScheme(
    primary = ShaktiRed,
    onPrimary = ShaktiCream,
    primaryContainer = Maroon100,
    onPrimaryContainer = ShaktiRedDark,
    secondary = ShaktiGoldDark,
    onSecondary = Color.White,
    secondaryContainer = ShaktiGoldLight,
    onSecondaryContainer = ShaktiDark,
    tertiary = Terracotta,
    onTertiary = Color.White,
    background = ShaktiCream,
    onBackground = ShaktiDark,
    surface = Color.White,
    onSurface = ShaktiDark,
    surfaceVariant = ShaktiCreamDark,
    onSurfaceVariant = ShaktiDark,
    outline = ShaktiGold.copy(alpha = 0.6f),
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false by default to preserve the signature Bengali Maroon/Gold branding
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
