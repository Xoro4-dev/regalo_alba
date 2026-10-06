package com.hugodev.horasconamor.ui.game

import kotlin.math.abs

internal data class PlatformGameState(
    val playerX: Float = PlatformGameEngine.PLAYER_START_X,
    val altitude: Float = 0f,
    val verticalVelocity: Float = 0f,
    val isGrounded: Boolean = true,
    val cameraX: Float = 0f,
    val elapsedSeconds: Float = 0f,
    val collectedBeers: Set<Int> = emptySet(),
    val lives: Int = PlatformGameEngine.STARTING_LIVES,
    val isWon: Boolean = false,
    val isGameOver: Boolean = false,
    val isInvulnerable: Boolean = false,
)

internal data class BeerPlacement(val x: Float, val height: Float)
internal data class PlatformPlacement(val x: Float, val width: Float, val height: Float)
internal data class EnemyPlacement(
    val centerX: Float,
    val patrolDistance: Float,
    val width: Float,
    val height: Float,
    val altitude: Float,
    val movementSpeed: Float,
    val phase: Float,
    val isFlying: Boolean,
) {
    fun positionAt(elapsedSeconds: Float): EnemyPosition {
        val motion = elapsedSeconds * movementSpeed + phase
        return EnemyPosition(
            x = centerX + kotlin.math.sin(motion) * patrolDistance,
            altitude = altitude + if (isFlying) kotlin.math.sin(motion * 1.8f) * 10f else 0f,
        )
    }
}

internal data class EnemyPosition(val x: Float, val altitude: Float)

internal class PlatformGameEngine(
    private val enemiesInLevel: List<EnemyPlacement> = enemies,
) {
    var state: PlatformGameState = PlatformGameState()
        private set

    private var direction = 0
    private var invulnerabilitySeconds = 0f

    fun holdDirection(direction: Int) {
        this.direction = direction.coerceIn(-1, 1)
    }

    fun jump() {
        if (state.isWon || state.isGameOver || !state.isGrounded) return
        state = state.copy(verticalVelocity = JUMP_SPEED, isGrounded = false)
    }

    fun restart() {
        direction = 0
        invulnerabilitySeconds = 0f
        state = PlatformGameState()
    }

    fun advance(elapsedSeconds: Float, viewportWidth: Float) {
        if (state.isWon || state.isGameOver || viewportWidth <= 0f) return

        val delta = elapsedSeconds.coerceIn(0f, MAX_FRAME_SECONDS)
        val previousX = state.playerX
        var playerX = (previousX + direction * MOVE_SPEED * delta)
            .coerceIn(PLAYER_WIDTH / 2f, LEVEL_WIDTH - PLAYER_WIDTH / 2f)
        val previousAltitude = state.altitude
        var altitude = previousAltitude
        var verticalVelocity = state.verticalVelocity
        var isGrounded = state.isGrounded

        if (isGrounded && altitude > 0f &&
            platforms.none { overlapsPlatform(playerX, it) && abs(it.height - altitude) < 1f }
        ) {
            isGrounded = false
        }

        if (!isGrounded) {
            verticalVelocity -= GRAVITY * delta
            altitude = (altitude + verticalVelocity * delta).coerceAtLeast(0f)

            if (verticalVelocity <= 0f) {
                val landingPlatform = platforms.firstOrNull { platform ->
                    overlapsPlatform(playerX, platform) &&
                        previousAltitude >= platform.height &&
                        altitude <= platform.height
                }
                if (landingPlatform != null) {
                    altitude = landingPlatform.height
                    verticalVelocity = 0f
                    isGrounded = true
                } else if (altitude == 0f) {
                    verticalVelocity = 0f
                    isGrounded = true
                }
            }
        }

        invulnerabilitySeconds = (invulnerabilitySeconds - delta).coerceAtLeast(0f)
        val elapsedSeconds = state.elapsedSeconds + delta
        val hitEnemy = invulnerabilitySeconds == 0f && enemiesInLevel.any { enemy ->
            val position = enemy.positionAt(elapsedSeconds)
            val playerOverlapsEnemyHorizontally =
                playerX + PLAYER_WIDTH / 2f > position.x - enemy.width / 2f &&
                    playerX - PLAYER_WIDTH / 2f < position.x + enemy.width / 2f
            val playerOverlapsEnemyVertically =
                altitude + PLAYER_HEIGHT > position.altitude &&
                    altitude < position.altitude + enemy.height
            playerOverlapsEnemyHorizontally && playerOverlapsEnemyVertically
        }
        var lives = state.lives
        if (hitEnemy) {
            lives -= 1
            playerX = (playerX - if (direction < 0) -KNOCKBACK_DISTANCE else KNOCKBACK_DISTANCE)
                .coerceIn(PLAYER_WIDTH / 2f, LEVEL_WIDTH - PLAYER_WIDTH / 2f)
            altitude = 0f
            verticalVelocity = 0f
            isGrounded = true
            invulnerabilitySeconds = INVULNERABILITY_DURATION
        }

        var collectedBeers = state.collectedBeers
        beers.forEachIndexed { index, beer ->
            if (index !in collectedBeers &&
                abs(playerX - beer.x) < BEER_PICKUP_RADIUS &&
                abs(altitude + PLAYER_HEIGHT / 2f - beer.height) < BEER_PICKUP_RADIUS
            ) {
                collectedBeers = collectedBeers + index
            }
        }

        val cameraX = (playerX - viewportWidth * CAMERA_LEAD).coerceIn(
            0f,
            (LEVEL_WIDTH - viewportWidth).coerceAtLeast(0f),
        )
        val isWon = collectedBeers.size >= BEER_GOAL
        val reachedExit = playerX >= LEVEL_WIDTH - PLAYER_WIDTH / 2f
        state = state.copy(
            playerX = playerX,
            altitude = altitude,
            verticalVelocity = verticalVelocity,
            isGrounded = isGrounded,
            cameraX = cameraX,
            elapsedSeconds = elapsedSeconds,
            collectedBeers = collectedBeers,
            lives = lives,
            isWon = isWon,
            isGameOver = lives <= 0 || reachedExit && !isWon,
            isInvulnerable = invulnerabilitySeconds > 0f,
        )
    }

    private fun overlapsPlatform(playerX: Float, platform: PlatformPlacement): Boolean =
        playerX + PLAYER_WIDTH / 2f > platform.x &&
            playerX - PLAYER_WIDTH / 2f < platform.x + platform.width

    companion object {
        const val LEVEL_WIDTH = 10_800f
        const val PLAYER_START_X = 42f
        const val PLAYER_WIDTH = 30f
        const val PLAYER_HEIGHT = 36f
        const val STARTING_LIVES = 3
        const val BEER_GOAL = 50
        const val BEER_PLACEMENT_COUNT = 70

        private const val MAX_FRAME_SECONDS = 0.05f
        private const val MOVE_SPEED = 205f
        private const val JUMP_SPEED = 570f
        private const val GRAVITY = 1_520f
        private const val KNOCKBACK_DISTANCE = 48f
        private const val INVULNERABILITY_DURATION = 1.1f
        private const val BEER_PICKUP_RADIUS = 30f
        private const val CAMERA_LEAD = 0.35f

        val platforms = (0 until 23).map { index ->
            val beerIndex = 2 + index * 3
            val beerX = 120f + beerIndex * 150f
            PlatformPlacement(
                x = beerX - 45f,
                width = 90f,
                height = listOf(48f, 76f, 58f, 92f, 64f, 84f, 42f, 100f)[index % 8],
            )
        }

        val beers = (0 until BEER_PLACEMENT_COUNT).map { index ->
            val x = 120f + index * 150f
            val platformIndex = (index - 2) / 3
            val platform = platforms.getOrNull(platformIndex)
                ?.takeIf { index >= 2 && (index - 2) % 3 == 0 }
            BeerPlacement(
                x = x,
                height = platform?.let { it.height + 18f } ?: 18f,
            )
        }

        val enemies = (0 until 36).map { index ->
            val isFlying = index % 3 == 2
            EnemyPlacement(
                centerX = 280f + index * 295f,
                patrolDistance = 24f + (index % 4) * 8f,
                width = if (isFlying) 34f else 40f,
                height = if (isFlying) 24f else 28f,
                altitude = if (isFlying) 48f + (index % 2) * 16f else 0f,
                movementSpeed = 1.2f + (index % 3) * 0.18f,
                phase = index * 0.9f,
                isFlying = isFlying,
            )
        }
    }
}
