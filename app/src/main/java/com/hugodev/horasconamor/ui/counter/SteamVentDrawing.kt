package com.hugodev.horasconamor.ui.counter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

internal fun DrawScope.drawSteamVents(progress: Float) {
    if (progress >= 1f) return

    val puffCount = 12
    val riseDistance = 56.dp.toPx()
    val ventInset = 24.dp.toPx()

    repeat(puffCount) { index ->
        val start = index * 0.035f
        val puffProgress = ((progress - start) / 0.78f).coerceIn(0f, 1f)
        if (progress < start || puffProgress >= 1f) return@repeat

        val fade = sin(puffProgress * PI).toFloat()
        val radius = (10.dp.toPx() + puffProgress * 17.dp.toPx()) * (0.8f + index % 3 * 0.12f)
        val rise = riseDistance * puffProgress
        val drift = (16.dp.toPx() + index % 4 * 3.dp.toPx()) * puffProgress
        val y = size.height * 0.77f - rise + (index % 5 - 2) * 9.dp.toPx()

        repeat(2) { side ->
            val isLeft = side == 0
            val x = if (isLeft) {
                ventInset - drift
            } else {
                size.width - ventInset + drift
            }
            val center = Offset(x, y)
            val alpha = (fade * 0.52f).coerceIn(0f, 0.52f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFEAF2FA).copy(alpha = alpha),
                        Color(0xFFD9E5F2).copy(alpha = alpha * 0.68f),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = radius * 1.45f,
                ),
                radius = radius * 1.45f,
                center = center,
            )
            drawCircle(
                color = Color(0xFFEAF2FA).copy(alpha = alpha * 0.48f),
                radius = radius * 0.72f,
                center = Offset(
                    x = x + if (isLeft) radius * 0.22f else -radius * 0.22f,
                    y = y - radius * 0.12f,
                ),
            )
        }
    }
}
