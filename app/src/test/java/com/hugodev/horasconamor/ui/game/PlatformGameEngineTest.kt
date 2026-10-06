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
    fun collidingWithEnemyConsumesOneLife() {
        val engine = PlatformGameEngine()
        engine.holdDirection(1)

        repeat(28) {
            engine.advance(0.05f, viewportWidth = 360f)
        }

        assertEquals(PlatformGameEngine.STARTING_LIVES - 1, engine.state.lives)
        assertTrue(engine.state.isInvulnerable)
    }

    @Test
    fun enemiesMoveAcrossTheLevel() {
        val enemy = PlatformGameEngine.enemies.first()

        assertTrue(enemy.positionAt(0f).x != enemy.positionAt(1f).x)
    }

    @Test
    fun jumpingOverAnEnemyAvoidsDamage() {
        val engine = PlatformGameEngine()
        engine.holdDirection(1)
        repeat(16) {
            engine.advance(0.05f, viewportWidth = 360f)
        }

        engine.jump()
        repeat(14) {
            engine.advance(0.05f, viewportWidth = 360f)
        }

        assertEquals(PlatformGameEngine.STARTING_LIVES, engine.state.lives)
    }

    @Test
    fun collectingFiftyBeersWins() {
        val engine = PlatformGameEngine(enemiesInLevel = emptyList())
        engine.holdDirection(1)

        for (frame in 0 until 5_000) {
            val nextRaisedBeer = PlatformGameEngine.beers
                .withIndex()
                .filter { (index, beer) ->
                    index !in engine.state.collectedBeers &&
                        beer.height > 18f &&
                        beer.x >= engine.state.playerX
                }
                .minByOrNull { (_, beer) -> beer.x }
            if (nextRaisedBeer != null &&
                nextRaisedBeer.value.x - engine.state.playerX < 140f &&
                engine.state.isGrounded
            ) {
                engine.jump()
            }
            engine.advance(0.05f, viewportWidth = 360f)
            if (engine.state.isWon || engine.state.isGameOver) break
        }

        assertTrue("Final state: ${engine.state}", engine.state.isWon)
        assertEquals(PlatformGameEngine.BEER_GOAL, engine.state.collectedBeers.size)
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
