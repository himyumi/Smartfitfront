package com.example.smartfit.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    secondary = OrangeGrey80,
    tertiary = Brown80,
    background = DarkBackground,    // The 0xFF121212 color
    surface = DarkBackground,       // Cards will take this bg by default if not overridden
    onPrimary = BlackText,          // Black text looks better on Orange80
    onBackground = WhiteText,       // White text on Dark background
    onSurface = WhiteText
)

private val LightColorScheme = lightColorScheme(
    primary = Orange40,
    secondaryContainer= Orange80, // Your main 0xFFFF9800 orange
    tertiaryContainer = Yellow40,
    secondary = OrangeGrey40,
    error = red40,
    tertiary = Brown40,
    background = LightBackground,   // White
    surface = LightBackground,
    onPrimary = WhiteText,          // White text on deep Orange
    onBackground = BlackText,       // Black text on White background
    onSurface = BlackText



    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun SmartfitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
