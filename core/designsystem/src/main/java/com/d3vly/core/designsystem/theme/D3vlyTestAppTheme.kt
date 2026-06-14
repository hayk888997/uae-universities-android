package com.d3vly.core.designsystem.theme

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
    primary = AppTealLight,
    onPrimary = AppNavyDark,
    secondary = AppSlate,
    onSecondary = AppNavyDark,
    tertiary = AppCoralLight,
    onTertiary = AppNavyDark,
    background = AppNavyDark,
    onBackground = AppWhite,
    surface = Color(0xFF0F263A),
    onSurface = AppWhite,
    surfaceVariant = Color(0xFF17364E),
    onSurfaceVariant = AppSlate,
    error = AppCoralLight,
)

private val LightColorScheme = lightColorScheme(
    primary = AppNavy,
    onPrimary = AppWhite,
    secondary = AppTeal,
    onSecondary = AppWhite,
    tertiary = AppCoral,
    onTertiary = AppWhite,
    background = AppCloud,
    onBackground = AppText,
    surface = AppWhite,
    onSurface = AppText,
    surfaceVariant = AppMist,
    onSurfaceVariant = AppTextSecondary,
    outline = AppTealDark,
    error = AppCoral,
)

@Composable
fun D3vlyTestAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
        content = content,
    )
}
