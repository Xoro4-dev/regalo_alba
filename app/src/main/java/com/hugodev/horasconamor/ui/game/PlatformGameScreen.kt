package com.hugodev.horasconamor.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hugodev.horasconamor.R
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun PlatformGameScreen() {
    val engine = remember { PlatformGameEngine() }
    val frameState = remember { mutableStateOf(engine.state) }
    val density = LocalDensity.current.density
    var viewportWidth by remember { mutableFloatStateOf(0f) }
    var beerCount by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(PlatformGameEngine.STARTING_LIVES) }
    var isFinished by remember { mutableStateOf(false) }
    var didWin by remember { mutableStateOf(false) }
    var gameLoopKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(engine, gameLoopKey) {
        var previousFrame = 0L
        while (true) {
            val hasFinished = withFrameNanos { frameTime ->
                if (previousFrame != 0L) {
                    val elapsed = (frameTime - previousFrame) / 1_000_000_000f
                    engine.advance(elapsed, viewportWidth)
                    val currentState = engine.state
                    frameState.value = currentState
                    beerCount = currentState.collectedBeers.size
                    lives = currentState.lives
                    if (currentState.isWon || currentState.isGameOver) {
                        isFinished = true
                        didWin = currentState.isWon
                    }
                }
                previousFrame = frameTime
                engine.state.isWon || engine.state.isGameOver
            }
            if (hasFinished) break
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
                text = stringResource(R.string.game_beer_score, beerCount, PlatformGameEngine.BEER_COUNT),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(PlatformGameEngine.STARTING_LIVES) { index ->
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = if (index < lives) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
                        },
                        modifier = Modifier.size(20.dp),
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
                    .padding(bottom = 2.dp)
                    .onSizeChanged {
                        viewportWidth = it.width / density
                    },
            ) {
                drawPlatformLevel(frameState.value, density)
            }
            if (isFinished) {
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
                            text = stringResource(
                                if (didWin) R.string.game_won_title else R.string.game_over_title,
                            ),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(
                                if (didWin) R.string.game_won_message else R.string.game_over_message,
                                beerCount,
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Button(
                            onClick = {
                                engine.restart()
                                frameState.value = engine.state
                                beerCount = 0
                                lives = PlatformGameEngine.STARTING_LIVES
                                isFinished = false
                                didWin = false
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HoldDirectionButton(label = "◀", direction = -1, engine = engine)
                HoldDirectionButton(label = "▶", direction = 1, engine = engine)
            }
            Button(
                onClick = engine::jump,
                modifier = Modifier.height(56.dp),
            ) {
                Text(
                    text = stringResource(R.string.game_jump),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun HoldDirectionButton(
    label: String,
    direction: Int,
    engine: PlatformGameEngine,
) {
    val accessibilityLabel = stringResource(
        if (direction < 0) R.string.game_move_left else R.string.game_move_right,
    )
    val latestEngine by rememberUpdatedState(engine)

    Surface(
        modifier = Modifier
            .size(64.dp)
            .semantics {
                contentDescription = accessibilityLabel
                role = Role.Button
            }
            .pointerInput(direction) {
                detectTapGestures(
                    onPress = {
                        latestEngine.holdDirection(direction)
                        try {
                            tryAwaitRelease()
                        } finally {
                            latestEngine.holdDirection(0)
                        }
                    },
                )
            },
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shadowElevation = 4.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun DrawScope.drawPlatformLevel(state: PlatformGameState, density: Float) {
    val viewportHeight = size.height / density
    val groundY = viewportHeight - 30f
    val scale = density

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF171635), Color(0xFF523F72), Color(0xFFEF9E7C)),
            endY = size.height,
        ),
    )
    drawCircle(
        color = Color(0xFFFFD990).copy(alpha = 0.92f),
        radius = 34.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(size.width * 0.78f, size.height * 0.2f),
    )
    drawFarHill(size.width, size.height * 0.63f, Color(0xFF84709B))
    drawFarHill(size.width * 1.2f, size.height * 0.7f, Color(0xFF504D78))

    withTransform({
        translate(left = -state.cameraX * scale, top = 0f)
    }) {
        drawRect(
            color = Color(0xFF283B3D),
            topLeft = androidx.compose.ui.geometry.Offset(0f, groundY * scale),
            size = androidx.compose.ui.geometry.Size(
                PlatformGameEngine.LEVEL_WIDTH * scale,
                size.height - groundY * scale,
            ),
        )
        drawRect(
            color = Color(0xFF96AE68),
            topLeft = androidx.compose.ui.geometry.Offset(0f, groundY * scale),
            size = androidx.compose.ui.geometry.Size(PlatformGameEngine.LEVEL_WIDTH * scale, 8.dp.toPx()),
        )

        PlatformGameEngine.platforms.forEach { platform ->
            val left = platform.x * scale
            val top = (groundY - platform.height) * scale
            drawRoundRect(
                color = Color(0xFF8D624D),
                topLeft = androidx.compose.ui.geometry.Offset(left, top),
                size = androidx.compose.ui.geometry.Size(platform.width * scale, 18.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
            )
            drawRoundRect(
                color = Color(0xFFB6C77B),
                topLeft = androidx.compose.ui.geometry.Offset(left, top - 4.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(platform.width * scale, 9.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(5.dp.toPx()),
            )
        }

        PlatformGameEngine.hazards.forEach { hazard ->
            val centerX = (hazard.x + hazard.width / 2f) * scale
            val top = (groundY - hazard.height) * scale
            drawRoundRect(
                color = Color(0xFF474052),
                topLeft = androidx.compose.ui.geometry.Offset(hazard.x * scale, top),
                size = androidx.compose.ui.geometry.Size(hazard.width * scale, hazard.height * scale),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
            )
            drawRoundRect(
                color = Color(0xFFFFC467),
                topLeft = androidx.compose.ui.geometry.Offset(centerX - 6.dp.toPx(), top - 5.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(12.dp.toPx(), 8.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
            )
        }

        PlatformGameEngine.beers.forEachIndexed { index, beer ->
            if (index !in state.collectedBeers) {
                drawBeer(beer.x * scale, (groundY - beer.height) * scale)
            }
        }

        drawHippieGirl(
            x = state.playerX * scale,
            feetY = (groundY - state.altitude) * scale,
            invulnerable = state.isInvulnerable,
        )
    }

    drawDaisy(22.dp.toPx(), groundY * scale - 13.dp.toPx(), 5.dp.toPx())
    drawDaisy(size.width - 34.dp.toPx(), groundY * scale - 13.dp.toPx(), 5.dp.toPx())
}

private fun DrawScope.drawFarHill(width: Float, y: Float, color: Color) {
    val path = Path().apply {
        moveTo(0f, y)
        cubicTo(width * 0.2f, y - 75.dp.toPx(), width * 0.34f, y + 45.dp.toPx(), width * 0.53f, y - 24.dp.toPx())
        cubicTo(width * 0.72f, y - 90.dp.toPx(), width * 0.82f, y + 24.dp.toPx(), width, y - 32.dp.toPx())
        lineTo(width, size.height)
        lineTo(0f, size.height)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawBeer(x: Float, centerY: Float) {
    val width = 18.dp.toPx()
    val height = 26.dp.toPx()
    drawRoundRect(
        color = Color(0xFFFFC84A),
        topLeft = androidx.compose.ui.geometry.Offset(x - width / 2f, centerY - height / 2f),
        size = androidx.compose.ui.geometry.Size(width, height),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
    )
    drawCircle(
        color = Color(0xFFFFF4D6),
        radius = 5.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(x - 4.dp.toPx(), centerY - height / 2f),
    )
    drawCircle(
        color = Color(0xFFFFF4D6),
        radius = 4.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(x + 3.dp.toPx(), centerY - height / 2f),
    )
    drawCircle(
        color = Color(0xFFFFC84A),
        radius = 5.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(x + width / 2f + 4.dp.toPx(), centerY),
        style = Stroke(width = 2.dp.toPx()),
    )
    drawRect(
        color = Color(0xFFFFF4D6).copy(alpha = 0.7f),
        topLeft = androidx.compose.ui.geometry.Offset(x - 5.dp.toPx(), centerY - 3.dp.toPx()),
        size = androidx.compose.ui.geometry.Size(2.dp.toPx(), 12.dp.toPx()),
    )
}

private fun DrawScope.drawHippieGirl(
    x: Float,
    feetY: Float,
    invulnerable: Boolean,
) {
    val alpha = if (invulnerable) 0.55f else 1f
    val legHeight = 14.dp.toPx()
    val bodyTop = feetY - 31.dp.toPx()

    drawRoundRect(
        color = Color(0xFF42A8A0).copy(alpha = alpha),
        topLeft = androidx.compose.ui.geometry.Offset(x - 9.dp.toPx(), feetY - legHeight),
        size = androidx.compose.ui.geometry.Size(7.dp.toPx(), legHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
    )
    drawRoundRect(
        color = Color(0xFF42A8A0).copy(alpha = alpha),
        topLeft = androidx.compose.ui.geometry.Offset(x + 2.dp.toPx(), feetY - legHeight),
        size = androidx.compose.ui.geometry.Size(7.dp.toPx(), legHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
    )
    drawRoundRect(
        color = Color(0xFF5A3C35).copy(alpha = alpha),
        topLeft = androidx.compose.ui.geometry.Offset(x - 11.dp.toPx(), feetY - 4.dp.toPx()),
        size = androidx.compose.ui.geometry.Size(10.dp.toPx(), 5.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
    )
    drawRoundRect(
        color = Color(0xFF5A3C35).copy(alpha = alpha),
        topLeft = androidx.compose.ui.geometry.Offset(x + 2.dp.toPx(), feetY - 4.dp.toPx()),
        size = androidx.compose.ui.geometry.Size(10.dp.toPx(), 5.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
    )
    drawRoundRect(
        color = Color(0xFFB06BD3).copy(alpha = alpha),
        topLeft = androidx.compose.ui.geometry.Offset(x - 11.dp.toPx(), bodyTop),
        size = androidx.compose.ui.geometry.Size(22.dp.toPx(), 16.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(7.dp.toPx()),
    )
    drawCircle(
        color = Color(0xFF4C302A).copy(alpha = alpha),
        radius = 11.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(x, bodyTop - 5.dp.toPx()),
    )
    drawCircle(
        color = Color(0xFFFFD1A6).copy(alpha = alpha),
        radius = 8.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(x + 1.dp.toPx(), bodyTop - 4.dp.toPx()),
    )
    drawArc(
        color = Color(0xFF4C302A).copy(alpha = alpha),
        startAngle = 180f,
        sweepAngle = 190f,
        useCenter = false,
        topLeft = androidx.compose.ui.geometry.Offset(x - 10.dp.toPx(), bodyTop - 17.dp.toPx()),
        size = androidx.compose.ui.geometry.Size(20.dp.toPx(), 20.dp.toPx()),
        style = Stroke(width = 5.dp.toPx()),
    )
    drawLine(
        color = Color(0xFFFF6FAE).copy(alpha = alpha),
        start = androidx.compose.ui.geometry.Offset(x - 8.dp.toPx(), bodyTop - 10.dp.toPx()),
        end = androidx.compose.ui.geometry.Offset(x + 8.dp.toPx(), bodyTop - 10.dp.toPx()),
        strokeWidth = 2.dp.toPx(),
    )
    drawDaisy(x + 7.dp.toPx(), bodyTop - 11.dp.toPx(), 4.dp.toPx(), alpha)
}

private fun DrawScope.drawDaisy(x: Float, y: Float, radius: Float, alpha: Float = 1f) {
    repeat(5) { petal ->
        val angle = petal * (2f * PI.toFloat() / 5f)
        val petalX = x + sin(angle) * radius * 0.7f
        val petalY = y + kotlin.math.cos(angle) * radius * 0.7f
        drawCircle(
            color = Color(0xFFFFF1D4).copy(alpha = alpha),
            radius = radius * 0.42f,
            center = androidx.compose.ui.geometry.Offset(petalX, petalY),
        )
    }
    drawCircle(
        color = Color(0xFFFFC84A).copy(alpha = alpha),
        radius = radius * 0.3f,
        center = androidx.compose.ui.geometry.Offset(x, y),
    )
}
