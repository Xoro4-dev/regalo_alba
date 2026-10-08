package com.hugodev.horasconamor.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hugodev.horasconamor.R
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun BeerCatchScreen() {
    val engine = remember { BeerCatchEngine() }
    val gameState = remember { mutableStateOf(engine.state) }
    val density = LocalDensity.current.density
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var score by remember { mutableIntStateOf(0) }
    var misses by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var gameLoopKey by remember { mutableIntStateOf(0) }
    val missesDescription = stringResource(
        if (misses == 1) R.string.game_miss_singular else R.string.game_miss_plural,
    )

    LaunchedEffect(engine, gameLoopKey, canvasSize) {
        if (canvasSize == IntSize.Zero) return@LaunchedEffect
        var previousFrame = 0L
        while (true) {
            val gameFinished = withFrameNanos { frameTime ->
                if (previousFrame != 0L) {
                    val elapsed = (frameTime - previousFrame) / 1_000_000_000f
                    engine.advance(
                        elapsedSeconds = elapsed,
                        width = canvasSize.width / density,
                        height = canvasSize.height / density,
                    )
                    val current = engine.state
                    gameState.value = current
                    score = current.score
                    misses = current.misses
                    isGameOver = current.isGameOver
                }
                previousFrame = frameTime
                engine.state.isGameOver
            }
            if (gameFinished) break
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(R.string.game_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.game_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.game_score, score),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Row(
                modifier = Modifier.semantics {
                    contentDescription = "$misses $missesDescription"
                },
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(BeerCatchEngine.MAX_MISSES) { index ->
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = if (index < BeerCatchEngine.MAX_MISSES - misses) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
                        },
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { canvasSize = it }
                    .pointerInput(density, canvasSize) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            engine.moveCatcherTo(change.position.x / density)
                        }
                    }
                    .pointerInput(density, canvasSize) {
                        detectTapGestures { position ->
                            engine.moveCatcherTo(position.x / density)
                        }
                    },
            ) {
                drawBeerCatchScene(gameState.value, density)
            }

            if (isGameOver) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 22.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.97f),
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.game_over_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(
                                if (score == 1) {
                                    R.string.game_over_message_singular
                                } else {
                                    R.string.game_over_message_plural
                                },
                                score,
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Button(
                            onClick = {
                                engine.restart()
                                gameState.value = engine.state
                                score = 0
                                misses = 0
                                isGameOver = false
                                gameLoopKey += 1
                            },
                        ) {
                            Text(stringResource(R.string.game_restart))
                        }
                    }
                }
            }
        }

        Text(
            text = stringResource(R.string.game_controls_hint),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun DrawScope.drawBeerCatchScene(state: BeerCatchState, density: Float) {
    val width = size.width
    val height = size.height
    val scale = density
    val time = state.elapsedSeconds

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF11162D), Color(0xFF282344), Color(0xFF583D68)),
            endY = height,
        ),
    )
    drawStars(time, width, height)
    drawMoon(time, width, height)
    drawCatchHills(width, height)

    val floorY = height - 24.dp.toPx()
    drawRect(
        color = Color(0xFF253D3A),
        topLeft = androidx.compose.ui.geometry.Offset(0f, floorY),
        size = androidx.compose.ui.geometry.Size(width, height - floorY),
    )
    drawRect(
        color = Color(0xFF9BB979),
        topLeft = androidx.compose.ui.geometry.Offset(0f, floorY),
        size = androidx.compose.ui.geometry.Size(width, 7.dp.toPx()),
    )

    state.beers.forEach { beer ->
        val center = androidx.compose.ui.geometry.Offset(beer.x * scale, beer.y * scale)
        drawCircle(
            color = Color(0xFFFFCE5B).copy(alpha = 0.18f),
            radius = 23.dp.toPx(),
            center = center,
        )
        drawFallingBeer(center.x, center.y)
    }
    drawCatchBasket(
        centerX = state.catcherX * scale,
        top = (height / scale - BeerCatchEngine.CATCHER_BOTTOM_MARGIN -
            BeerCatchEngine.CATCHER_HEIGHT) * scale,
        scale = scale,
    )
}

private fun DrawScope.drawStars(time: Float, width: Float, height: Float) {
    repeat(24) { index ->
        val x = ((index * 83f) % width)
        val y = ((index * 127f) % (height * 0.62f))
        val twinkle = 0.35f + (sin(time * 1.7f + index) + 1f) * 0.25f
        drawCircle(
            color = Color(0xFFFFE8BC).copy(alpha = twinkle),
            radius = (1.1f + index % 3) * density,
            center = androidx.compose.ui.geometry.Offset(x, y),
        )
    }
}

private fun DrawScope.drawMoon(time: Float, width: Float, height: Float) {
    val moonX = width * (0.78f + sin(time * 0.12f) * 0.025f)
    val moonY = height * 0.16f
    drawCircle(
        color = Color(0xFFFFD78F).copy(alpha = 0.12f),
        radius = 43.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(moonX, moonY),
    )
    drawCircle(
        color = Color(0xFFFFD78F),
        radius = 25.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(moonX, moonY),
    )
    repeat(3) { crater ->
        drawCircle(
            color = Color(0xFFE4B96F).copy(alpha = 0.55f),
            radius = (3 + crater).dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(
                moonX + ((crater - 1) * 9).dp.toPx(),
                moonY + ((crater % 2) * 8 - 3).dp.toPx(),
            ),
        )
    }
}

private fun DrawScope.drawCatchHills(width: Float, height: Float) {
    val horizon = height * 0.77f
    listOf(Color(0xFF54446D), Color(0xFF393C60)).forEachIndexed { index, color ->
        val offset = index * 34.dp.toPx()
        val path = Path().apply {
            moveTo(0f, horizon + offset)
            cubicTo(
                width * 0.25f, horizon - 90.dp.toPx() + offset,
                width * 0.45f, horizon + 60.dp.toPx() + offset,
                width * 0.72f, horizon - 40.dp.toPx() + offset,
            )
            cubicTo(
                width * 0.86f, horizon - 86.dp.toPx() + offset,
                width * 0.94f, horizon + 18.dp.toPx() + offset,
                width, horizon - 35.dp.toPx() + offset,
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(path, color)
    }
}

private fun DrawScope.drawFallingBeer(x: Float, centerY: Float) {
    val canWidth = 22.dp.toPx()
    val canHeight = 34.dp.toPx()
    drawRoundRect(
        color = Color(0xFFF3B83F),
        topLeft = androidx.compose.ui.geometry.Offset(x - canWidth / 2f, centerY - canHeight / 2f),
        size = androidx.compose.ui.geometry.Size(canWidth, canHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
    )
    drawRoundRect(
        color = Color(0xFFFFE5A6),
        topLeft = androidx.compose.ui.geometry.Offset(x - canWidth / 2f, centerY - canHeight / 2f),
        size = androidx.compose.ui.geometry.Size(canWidth, 6.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
    )
    drawRect(
        color = Color(0xFFFFF4D5),
        topLeft = androidx.compose.ui.geometry.Offset(x - 2.dp.toPx(), centerY - 5.dp.toPx()),
        size = androidx.compose.ui.geometry.Size(4.dp.toPx(), 15.dp.toPx()),
    )
    drawCircle(
        color = Color(0xFFFFE5A6),
        radius = 2.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(x, centerY + canHeight / 2f),
    )
}

private fun DrawScope.drawCatchBasket(centerX: Float, top: Float, scale: Float) {
    val basketWidth = BeerCatchEngine.CATCHER_WIDTH * scale
    val basketHeight = BeerCatchEngine.CATCHER_HEIGHT * scale
    val left = centerX - basketWidth / 2f
    val cornerRadius = 8.dp.toPx()

    drawRoundRect(
        color = Color(0xFFFFD276).copy(alpha = 0.25f),
        topLeft = androidx.compose.ui.geometry.Offset(left - 8.dp.toPx(), top - 10.dp.toPx()),
        size = androidx.compose.ui.geometry.Size(basketWidth + 16.dp.toPx(), basketHeight + 18.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
    )
    drawRoundRect(
        color = Color(0xFFB97549),
        topLeft = androidx.compose.ui.geometry.Offset(left, top),
        size = androidx.compose.ui.geometry.Size(basketWidth, basketHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius),
    )
    drawRoundRect(
        color = Color(0xFFE1A765),
        topLeft = androidx.compose.ui.geometry.Offset(left, top),
        size = androidx.compose.ui.geometry.Size(basketWidth, 10.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(5.dp.toPx()),
    )
    repeat(5) { slat ->
        val x = left + (slat + 0.5f) * basketWidth / 5f
        drawLine(
            color = Color(0xFF8F563D),
            start = androidx.compose.ui.geometry.Offset(x, top + 12.dp.toPx()),
            end = androidx.compose.ui.geometry.Offset(x, top + basketHeight - 4.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
        )
    }
}
