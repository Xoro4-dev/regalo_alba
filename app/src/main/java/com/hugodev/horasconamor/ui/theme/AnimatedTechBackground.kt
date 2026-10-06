package com.hugodev.horasconamor.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedTechBackground(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "ambient-background")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "background-drift",
    )
    val counterDrift by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "background-counter-drift",
    )
    val background = MaterialTheme.colorScheme.background
    val cyan = MaterialTheme.colorScheme.primary.copy(alpha = 0.13f)
    val violet = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
    val gridColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f)

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(background)
        val gridStep = 36.dp.toPx()
        var x = 0f
        while (x < size.width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 0.7.dp.toPx(),
            )
            x += gridStep
        }
        var y = 0f
        while (y < size.height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 0.7.dp.toPx(),
            )
            y += gridStep
        }

        val firstCenter = Offset(
            x = size.width * (0.18f + drift * 0.18f),
            y = size.height * (0.2f + counterDrift * 0.08f),
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(cyan, Color.Transparent),
                center = firstCenter,
                radius = 280.dp.toPx(),
            ),
            radius = 280.dp.toPx(),
            center = firstCenter,
        )

        val secondCenter = Offset(
            x = size.width * (0.86f - counterDrift * 0.24f),
            y = size.height * (0.72f - drift * 0.13f),
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(violet, Color.Transparent),
                center = secondCenter,
                radius = 320.dp.toPx(),
            ),
            radius = 320.dp.toPx(),
            center = secondCenter,
        )

        val orbitCenter = Offset(size.width * 0.82f, size.height * 0.23f)
        drawCircle(
            color = cyan.copy(alpha = 0.2f),
            radius = 72.dp.toPx(),
            center = orbitCenter,
            style = Stroke(width = 1.dp.toPx()),
        )
        drawCircle(
            color = violet.copy(alpha = 0.17f),
            radius = 88.dp.toPx(),
            center = orbitCenter,
            style = Stroke(width = 1.dp.toPx()),
        )
        drawCircle(
            color = cyan.copy(alpha = 0.5f),
            radius = 3.dp.toPx(),
            center = Offset(
                x = orbitCenter.x + 80.dp.toPx() * cos(drift * 6.28f),
                y = orbitCenter.y + 80.dp.toPx() * sin(drift * 6.28f),
            ),
        )
    }
}
