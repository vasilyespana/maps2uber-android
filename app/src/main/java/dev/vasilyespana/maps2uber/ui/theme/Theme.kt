package dev.vasilyespana.maps2uber.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BrandBlack = Color(0xFF111111)
private val BrandGreen = Color(0xFF06C167)
private val PinRed = Color(0xFFEA4335)

private val LightColors = lightColorScheme(
    primary = BrandGreen,
    onPrimary = Color.White,
    secondary = PinRed,
    surface = Color.White,
    onSurface = BrandBlack,
)

private val DarkColors = darkColorScheme(
    primary = BrandGreen,
    onPrimary = BrandBlack,
    secondary = PinRed,
    surface = BrandBlack,
    onSurface = Color.White,
)

@Composable
fun Maps2UberTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
