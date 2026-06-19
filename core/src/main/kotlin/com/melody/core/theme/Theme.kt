package com.melody.core.theme

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
    primary = DarkPrimary,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = DarkBackground,
    onSecondary = LightBackground,
    onBackground = LightBackground,
    onSurface = LightBackground,
    onSurfaceVariant = LightBackground
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onPrimary = LightSurface,
    onSecondary = LightSurface,
    onBackground = DarkBackground,
    onSurface = DarkBackground,
    onSurfaceVariant = DarkBackground
)

/**
 * Main theme composable for Melody.
 *
 * @param darkTheme       Whether to apply a dark color scheme.
 * @param dynamicColor    Use wallpaper-derived colors on Android 12+ (API 31+).
 * @param customPrimaryColor When provided, overrides the primary (and derived) colors in the
 *                           scheme. Has no effect when [dynamicColor] is active.
 */
@Composable
fun MelodyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    customPrimaryColor: Color? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> {
            if (customPrimaryColor != null) {
                DarkColorScheme.copy(
                    primary = customPrimaryColor,
                    onPrimary = Color.White,
                    primaryContainer = customPrimaryColor.copy(alpha = 0.2f),
                    onPrimaryContainer = customPrimaryColor
                )
            } else {
                DarkColorScheme
            }
        }
        else -> {
            if (customPrimaryColor != null) {
                LightColorScheme.copy(
                    primary = customPrimaryColor,
                    onPrimary = Color.White,
                    primaryContainer = customPrimaryColor.copy(alpha = 0.15f),
                    onPrimaryContainer = customPrimaryColor
                )
            } else {
                LightColorScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
