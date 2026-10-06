package com.hugodev.horasconamor.ui.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlatformGameEngineTest {
    @Test
    fun jumpRisesAndReturnsToGround() {
        val engine = PlatformGameEngine()

        engine.jump()
        repeat(30) {
            engine.advance(0.05f, viewportWidth = 360f)
        }

        assertEquals(0f, engine.state.altitude, 0.01f)
        assertEquals(0f, engine.state.verticalVelocity, 0.01f)
    }

    @Test
    fun movingThroughBeerCollectsItOnlyOnce() {
        val engine = PlatformGameEngine()
        engine.holdDirection(1)

        repeat(8) {
            engine.advance(0.05f, viewportWidth = 360f)
        }
        val firstCount = engine.state.collectedBeers.size
        repeat(8) {
            engine.advance(0.05f, viewportWidth = 360f)
        }

        assertTrue(firstCount > 0)
        assertEquals(firstCount, engine.state.collectedBeers.size)
    }

    @Test
    fun collidingWithHazardConsumesOneLife() {
        val engine = PlatformGameEngine()
        engine.holdDirection(1)

        repeat(30) {
            engine.advance(0.05f, viewportWidth = 360f)
        }

        assertEquals(PlatformGameEngine.STARTING_LIVES - 1, engine.state.lives)
        assertTrue(engine.state.isInvulnerable)
    }

    @Test
    fun jumpingOverHazardsCanReachTheFinish() {
        val engine = PlatformGameEngine()
        engine.holdDirection(1)
        val damagePositions = mutableListOf<Float>()
        var previousLives = engine.state.lives

        repeat(1_000) {
            val nextHazard = PlatformGameEngine.hazards.firstOrNull {
                it.x + it.width >= engine.state.playerX
            }
            if (nextHazard != null &&
                nextHazard.x - engine.state.playerX < 85f &&
                engine.state.isGrounded
            ) {
                engine.jump()
            }
            engine.advance(0.05f, viewportWidth = 360f)
            if (engine.state.lives < previousLives) {
                damagePositions += engine.state.playerX
                previousLives = engine.state.lives
            }
        }

        assertTrue("Final state: ${engine.state}; damage at $damagePositions", engine.state.isWon)
        assertTrue(engine.state.lives > 0)
    }

    @Test
    fun gameOverCanBeRestarted() {
        val engine = PlatformGameEngine()
        engine.holdDirection(1)

        repeat(1_000) {
            engine.advance(0.05f, viewportWidth = 360f)
        }
        assertTrue(engine.state.isGameOver)

        engine.restart()

        assertEquals(PlatformGameEngine.PLAYER_START_X, engine.state.playerX)
        assertEquals(PlatformGameEngine.STARTING_LIVES, engine.state.lives)
        assertTrue(engine.state.collectedBeers.isEmpty())
        assertTrue(!engine.state.isGameOver)
    }
}
