package com.hugodev.horasconamor.ui.theme

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
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = Rose,
    onPrimary = Cream,
    primaryContainer = RoseContainer,
    onPrimaryContainer = Ink,
    secondary = Violet,
    onSecondary = Cream,
    secondaryContainer = VioletContainer,
    onSecondaryContainer = Ink,
    tertiary = AlarmRed,
    tertiaryContainer = AlarmRedContainer,
    onTertiaryContainer = Ink,
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
    secondary = VioletDark,
    onSecondary = CreamDark,
    secondaryContainer = VioletContainerDark,
    onSecondaryContainer = InkDark,
    tertiary = AlarmRedDark,
    tertiaryContainer = Color(0xFF421A27),
    onTertiaryContainer = InkDark,
    background = CreamDark,
    surface = CreamDark,
    onBackground = InkDark,
    onSurface = InkDark,
)

@Composable
fun HorasConAmorTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
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
        shapes = androidx.compose.material3.Shapes(
            extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
            small = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
            medium = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
            large = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
            extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(36.dp),
        ),
        content = content,
    )
}
