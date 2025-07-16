package com.edu.achadosufc.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ufcAzulClaro,
    secondary = ufcAmareloDestaque,
    tertiary = ufcAzulPrincipal,
    background = ufcCinzaEscuro,
    surface = ufcCinzaEscuro,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ufcAzulPrincipal,
    secondary = ufcAmareloDestaque,
    tertiary = ufcAzulClaro,
    background = ufcCinzaClaro,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = ufcCinzaEscuro,
    onSurface = ufcCinzaEscuro
)

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

@Composable
fun AchadosUFCTheme(
    themeMode: ThemeMode = ThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val isSystemInDark = isSystemInDarkTheme()

    val colorScheme = when (themeMode) {
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.SYSTEM -> {
            val context = LocalContext.current
            if (isSystemInDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            val useLightIcons = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> !isSystemInDark
            }

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                !useLightIcons
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                !useLightIcons
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}