package com.psydrite.lofigram.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

@Composable
fun ShimmerBrush(colorstart: Color, colormiddle: Color = MaterialTheme.colorScheme.onSecondary): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = -800f,
        targetValue = 2000f, // must exceed screen width
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5500, easing = LinearEasing)
        ),
        label = "shimmerAnim"
    )

    return Brush.linearGradient(
        colors = listOf(
            colorstart,
            colormiddle.copy(0.6f),
            colormiddle,
            colormiddle.copy(0.6f),
            colorstart
        ),
        start = Offset(translateAnim.value - 300f , -200f),
        end = Offset(translateAnim.value + 300f, +200f), // wide span, not mirrored
        tileMode = TileMode.Clamp // no gaps
    )
}
