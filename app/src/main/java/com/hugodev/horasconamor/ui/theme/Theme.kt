package com.hugodev.horasconamor.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Rose,
    onPrimary = Cream,
    primaryContainer = RoseContainer,
    onPrimaryContainer = Ink,
    background = Cream,
    surface = Cream,
    onBackground = Ink,
    onSurface = Ink,
)

private val DarkColorScheme = darkColorScheme(
    primary = RoseDark,
    onPrimary = InkDark,
    primaryContainer = RoseContainerDark,
    onPrimaryContainer = Cream,
    background = CreamDark,
    surface = CreamDark,
    onBackground = InkDark,
    onSurface = InkDark,
)

@Composable
fun HorasConAmorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
