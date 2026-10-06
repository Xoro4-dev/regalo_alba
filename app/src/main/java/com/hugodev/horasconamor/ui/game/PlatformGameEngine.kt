package com.hugodev.horasconamor.ui.game

import kotlin.math.abs

internal data class PlatformGameState(
    val playerX: Float = PlatformGameEngine.PLAYER_START_X,
    val altitude: Float = 0f,
    val verticalVelocity: Float = 0f,
    val isGrounded: Boolean = true,
    val cameraX: Float = 0f,
    val collectedBeers: Set<Int> = emptySet(),
    val lives: Int = PlatformGameEngine.STARTING_LIVES,
    val isWon: Boolean = false,
    val isGameOver: Boolean = false,
    val isInvulnerable: Boolean = false,
)

internal data class BeerPlacement(val x: Float, val height: Float)
internal data class PlatformPlacement(val x: Float, val width: Float, val height: Float)
internal data class HazardPlacement(val x: Float, val width: Float, val height: Float)

internal class PlatformGameEngine {
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
        val hitHazard = invulnerabilitySeconds == 0f && hazards.any { hazard ->
            playerX + PLAYER_WIDTH / 2f > hazard.x &&
                playerX - PLAYER_WIDTH / 2f < hazard.x + hazard.width &&
                altitude < hazard.height
        }
        var lives = state.lives
        if (hitHazard) {
            lives -= 1
            playerX = (playerX - if (direction < 0) -KNOCKBACK_DISTANCE else KNOCKBACK_DISTANCE)
                .coerceAtLeast(PLAYER_WIDTH / 2f)
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
        val isWon = playerX >= LEVEL_WIDTH - PLAYER_WIDTH / 2f
        state = state.copy(
            playerX = playerX,
            altitude = altitude,
            verticalVelocity = verticalVelocity,
            isGrounded = isGrounded,
            cameraX = cameraX,
            collectedBeers = collectedBeers,
            lives = lives,
            isWon = isWon,
            isGameOver = lives <= 0,
            isInvulnerable = invulnerabilitySeconds > 0f,
        )
    }

    private fun overlapsPlatform(playerX: Float, platform: PlatformPlacement): Boolean =
        playerX + PLAYER_WIDTH / 2f > platform.x &&
            playerX - PLAYER_WIDTH / 2f < platform.x + platform.width

    companion object {
        const val LEVEL_WIDTH = 2_200f
        const val PLAYER_START_X = 42f
        const val PLAYER_WIDTH = 30f
        const val PLAYER_HEIGHT = 36f
        const val STARTING_LIVES = 3
        const val BEER_COUNT = 12

        private const val MAX_FRAME_SECONDS = 0.05f
        private const val MOVE_SPEED = 205f
        private const val JUMP_SPEED = 570f
        private const val GRAVITY = 1_520f
        private const val KNOCKBACK_DISTANCE = 48f
        private const val INVULNERABILITY_DURATION = 1.1f
        private const val BEER_PICKUP_RADIUS = 30f
        private const val CAMERA_LEAD = 0.35f

        val platforms = listOf(
            PlatformPlacement(185f, 105f, 68f),
            PlatformPlacement(445f, 110f, 94f),
            PlatformPlacement(720f, 120f, 72f),
            PlatformPlacement(1_015f, 110f, 100f),
            PlatformPlacement(1_315f, 120f, 82f),
            PlatformPlacement(1_620f, 115f, 106f),
            PlatformPlacement(1_900f, 115f, 76f),
        )

        val beers = listOf(
            BeerPlacement(122f, 27f),
            BeerPlacement(230f, 98f),
            BeerPlacement(365f, 27f),
            BeerPlacement(490f, 124f),
            BeerPlacement(625f, 27f),
            BeerPlacement(770f, 102f),
            BeerPlacement(930f, 27f),
            BeerPlacement(1_065f, 130f),
            BeerPlacement(1_220f, 27f),
            BeerPlacement(1_370f, 112f),
            BeerPlacement(1_700f, 138f),
            BeerPlacement(1_940f, 108f),
        )

        val hazards = listOf(
            HazardPlacement(315f, 34f, 28f),
            HazardPlacement(590f, 38f, 32f),
            HazardPlacement(860f, 38f, 30f),
            HazardPlacement(1_165f, 40f, 34f),
            HazardPlacement(1_505f, 40f, 32f),
            HazardPlacement(1_800f, 42f, 36f),
            HazardPlacement(2_060f, 38f, 30f),
        )
    }
}
