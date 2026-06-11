package com.d3vly.testapp.ui.theme

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
    primary = TammTealLight,
    onPrimary = TammNavyDark,
    secondary = TammSlate,
    onSecondary = TammNavyDark,
    tertiary = TammCoralLight,
    onTertiary = TammNavyDark,
    background = TammNavyDark,
    onBackground = TammWhite,
    surface = Color(0xFF0F263A),
    onSurface = TammWhite,
    surfaceVariant = Color(0xFF17364E),
    onSurfaceVariant = TammSlate,
    error = TammCoralLight,
)

private val LightColorScheme = lightColorScheme(
    primary = TammNavy,
    onPrimary = TammWhite,
    secondary = TammTeal,
    onSecondary = TammWhite,
    tertiary = TammCoral,
    onTertiary = TammWhite,
    background = TammCloud,
    onBackground = TammText,
    surface = TammWhite,
    onSurface = TammText,
    surfaceVariant = TammMist,
    onSurfaceVariant = TammTextSecondary,
    outline = TammTealDark,
    error = TammCoral,
)

@Composable
fun D3vlyTestAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
