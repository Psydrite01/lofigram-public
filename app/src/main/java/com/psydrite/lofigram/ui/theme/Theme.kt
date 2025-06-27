package com.psydrite.lofigram.ui.theme

import android.app.Activity
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
    primary = Color(0xff006ffd),            //buttons and all
    onPrimary = Color.Black,             //text on buttons etc

    secondary = Color(0xff202020),           //different shade of primary
    onSecondary = Color.Yellow,    //glowing color, for premium effect

    tertiary = Color(0xff282828),           //some cards and borders

    background = Color(0xff0e0e0e),
    onBackground = Color.White,           //text on background

)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xff006ffd),            //buttons and all
    onPrimary = Color.White,             //text on buttons etc

    secondary = Color(0xffeaf2ff),           //different shade of primary
    onSecondary = Color(0xffB8860B),

    tertiary = Color(0xffe8e9eb),           //some cards and borders

    background = Color.White,
    onBackground = Color.Black,           //text on background
)

@Composable
fun LofigramTheme(
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