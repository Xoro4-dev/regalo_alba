package com.hugodev.horasconamor.ui.counter

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal fun DrawScope.drawHourBurst(progress: Float) {
    if (progress >= 1f) return
    val colors = listOf(
        Color(0xFFFF6B9B),
        Color(0xFFB7A8FF),
        Color(0xFFFFD166),
        Color(0xFF5CEBFF),
    )
    val burstCenter = center
    val easedProgress = 1f - (1f - progress) * (1f - progress)
    val maxRadius = size.minDimension * 0.5f

    repeat(20) { index ->
        val angle = (2.0 * PI * index / 20.0) - PI / 2.0
        val distance = maxRadius * easedProgress * (0.82f + (index % 4) * 0.07f)
        val x = burstCenter.x + cos(angle).toFloat() * distance
        val y = burstCenter.y + sin(angle).toFloat() * distance
        val alpha = ((1f - progress) / 0.68f).coerceIn(0f, 1f)
        val color = colors[index % colors.size].copy(alpha = alpha)
        val radius = (5f + (index % 4) * 1.5f) * density

        rotate(degrees = index * 18f + easedProgress * 220f, pivot = Offset(x, y)) {
            drawRoundRect(
                color = color,
                topLeft = Offset(x - radius * 0.5f, y - radius * 1.5f),
                size = Size(radius, radius * 3f),
                cornerRadius = CornerRadius(radius),
            )
        }
    }

    drawCircle(
        color = Color.White.copy(alpha = ((1f - progress) / 0.68f).coerceIn(0f, 1f) * 0.7f),
        radius = maxRadius * (0.1f + easedProgress * 0.9f),
        center = burstCenter,
        style = Stroke(width = 3.dp.toPx()),
    )
}
