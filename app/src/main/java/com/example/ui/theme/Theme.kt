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
    primary = Gold400,
    onPrimary = Maroon900,
    primaryContainer = Maroon800,
    onPrimaryContainer = Gold200,
    secondary = TerracottaLight,
    onSecondary = DarkCharcoal,
    secondaryContainer = Maroon700,
    onSecondaryContainer = WarmCream,
    tertiary = VermilionLight,
    onTertiary = Color.White,
    background = DarkCharcoal,
    onBackground = WarmCream,
    surface = DarkSurface,
    onSurface = WarmCream,
    surfaceVariant = DarkCard,
    onSurfaceVariant = Gold200,
    outline = Gold700.copy(alpha = 0.5f),
)

private val LightColorScheme = lightColorScheme(
    primary = Maroon700,
    onPrimary = Gold100,
    primaryContainer = Maroon100,
    onPrimaryContainer = Maroon900,
    secondary = Gold700,
    onSecondary = Color.White,
    secondaryContainer = Gold100,
    onSecondaryContainer = Gold900,
    tertiary = VermilionRed,
    onTertiary = Color.White,
    background = WarmCream,
    onBackground = Maroon900,
    surface = IvoryWhite,
    onSurface = Maroon900,
    surfaceVariant = SoftSand,
    onSurfaceVariant = Maroon800,
    outline = Gold500.copy(alpha = 0.4f),
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
