package com.hugodev.horasconamor.ui.game

import kotlin.math.abs

internal data class FallingBeer(
    val id: Int,
    val x: Float,
    val y: Float,
    val speed: Float,
)

internal data class BeerCatchState(
    val catcherX: Float = 0f,
    val beers: List<FallingBeer> = emptyList(),
    val score: Int = 0,
    val misses: Int = 0,
    val elapsedSeconds: Float = 0f,
    val isGameOver: Boolean = false,
    val lastCaughtId: Int = -1,
)

internal class BeerCatchEngine {
    var state = BeerCatchState()
        private set

    private var spawnElapsed = 0f
    private var nextBeerId = 0
    private var viewportWidth = 0f

    fun moveCatcherTo(x: Float) {
        if (viewportWidth <= 0f || state.isGameOver) return
        state = state.copy(
            catcherX = x.coerceIn(CATCHER_WIDTH / 2f, viewportWidth - CATCHER_WIDTH / 2f),
        )
    }

    fun advance(elapsedSeconds: Float, width: Float, height: Float) {
        if (state.isGameOver || width <= 0f || height <= 0f) return
        viewportWidth = width
        val delta = elapsedSeconds.coerceIn(0f, MAX_FRAME_SECONDS)
        val elapsed = state.elapsedSeconds + delta
        val catcherX = if (state.catcherX == 0f) width / 2f else state.catcherX
        spawnElapsed += delta

        val fallingSpeed = (INITIAL_FALL_SPEED + state.score * SPEED_PER_POINT)
            .coerceAtMost(MAX_FALL_SPEED)
        val spawnInterval = (INITIAL_SPAWN_INTERVAL - state.score * SPAWN_INTERVAL_REDUCTION)
            .coerceAtLeast(MIN_SPAWN_INTERVAL)
        val beers = state.beers.toMutableList()
        var newScore = state.score
        var newMisses = state.misses
        var lastCaughtId = state.lastCaughtId

        while (spawnElapsed >= spawnInterval) {
            spawnElapsed -= spawnInterval
            val id = nextBeerId++
            val availableWidth = (width - BEER_WIDTH).coerceAtLeast(1f)
            val deterministicSlot = ((id * 73) % 97) / 96f
            val x = BEER_WIDTH / 2f + deterministicSlot * (availableWidth - BEER_WIDTH / 2f)
            beers += FallingBeer(
                id = id,
                x = x,
                y = -BEER_HEIGHT,
                speed = fallingSpeed,
            )
        }

        val catcherTop = height - CATCHER_BOTTOM_MARGIN - CATCHER_HEIGHT
        val remainingBeers = mutableListOf<FallingBeer>()
        beers.forEach { beer ->
            val nextY = beer.y + beer.speed * delta
            val reachesCatcher = nextY + BEER_HEIGHT >= catcherTop &&
                nextY <= catcherTop + CATCHER_HEIGHT &&
                abs(beer.x - catcherX) <= (CATCHER_WIDTH + BEER_WIDTH) / 2f

            when {
                reachesCatcher -> {
                    newScore += 1
                    lastCaughtId = beer.id
                }
                nextY > height -> newMisses += 1
                else -> remainingBeers += beer.copy(y = nextY)
            }
        }

        state = state.copy(
            catcherX = catcherX,
            beers = remainingBeers,
            score = newScore,
            misses = newMisses,
            elapsedSeconds = elapsed,
            isGameOver = newMisses >= MAX_MISSES,
            lastCaughtId = lastCaughtId,
        )
    }

    fun restart() {
        spawnElapsed = 0f
        nextBeerId = 0
        state = BeerCatchState(catcherX = viewportWidth / 2f)
    }

    companion object {
        const val CATCHER_WIDTH = 112f
        const val CATCHER_HEIGHT = 34f
        const val CATCHER_BOTTOM_MARGIN = 34f
        const val BEER_WIDTH = 26f
        const val BEER_HEIGHT = 38f
        const val MAX_MISSES = 3

        private const val MAX_FRAME_SECONDS = 0.05f
        private const val INITIAL_FALL_SPEED = 190f
        private const val SPEED_PER_POINT = 7f
        private const val MAX_FALL_SPEED = 520f
        private const val INITIAL_SPAWN_INTERVAL = 1.3f
        private const val SPAWN_INTERVAL_REDUCTION = 0.012f
        private const val MIN_SPAWN_INTERVAL = 0.42f
    }
}
