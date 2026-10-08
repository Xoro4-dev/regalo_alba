package com.hugodev.horasconamor.ui.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BeerCatchEngineTest {
    @Test
    fun movingBasketClampsToPlayArea() {
        val engine = BeerCatchEngine()
        engine.advance(0.05f, width = 360f, height = 640f)

        engine.moveCatcherTo(-100f)
        assertEquals(BeerCatchEngine.CATCHER_WIDTH / 2f, engine.state.catcherX, 0.01f)

        engine.moveCatcherTo(500f)
        assertEquals(360f - BeerCatchEngine.CATCHER_WIDTH / 2f, engine.state.catcherX, 0.01f)
    }

    @Test
    fun catchingFallingBeerIncreasesScore() {
        val engine = BeerCatchEngine()
        engine.advance(0.05f, width = 360f, height = 640f)
        engine.moveCatcherTo(56f)

        repeat(120) {
            engine.advance(0.05f, width = 360f, height = 640f)
        }

        assertTrue("Expected the basket to catch beer: ${engine.state}", engine.state.score > 0)
        assertEquals(0, engine.state.misses)
    }

    @Test
    fun fallingBeersAccelerateAsTimePasses() {
        val engine = BeerCatchEngine()

        repeat(30) {
            engine.advance(0.05f, width = 360f, height = 10_000f)
        }
        val firstBeer = engine.state.beers.first { it.id == 0 }
        repeat(20) {
            engine.advance(0.05f, width = 360f, height = 10_000f)
        }
        val fasterBeer = engine.state.beers.first { it.id == 0 }

        assertTrue(fasterBeer.speed > firstBeer.speed)
    }

    @Test
    fun fallingCansUseDifferentOriginalDesigns() {
        val engine = BeerCatchEngine()
        repeat(120) {
            engine.advance(0.05f, width = 360f, height = 10_000f)
        }

        assertEquals(BeerCatchEngine.CAN_DESIGN_COUNT, engine.state.beers.map { it.design }.distinct().size)
    }

    @Test
    fun beerFallingOutsideBasketCountsAsMiss() {
        val engine = BeerCatchEngine()
        engine.advance(0.05f, width = 360f, height = 640f)
        engine.moveCatcherTo(304f)

        repeat(200) {
            engine.advance(0.05f, width = 360f, height = 640f)
        }

        assertTrue("Expected missed beers: ${engine.state}", engine.state.misses > 0)
    }

    @Test
    fun threeMissedBeersEndTheGame() {
        val engine = BeerCatchEngine()
        engine.advance(0.05f, width = 360f, height = 640f)
        engine.moveCatcherTo(304f)

        for (frame in 0 until 400) {
            engine.advance(0.05f, width = 360f, height = 640f)
            if (engine.state.isGameOver) break
        }

        assertEquals(BeerCatchEngine.MAX_MISSES, engine.state.misses)
        assertTrue(engine.state.isGameOver)
    }

    @Test
    fun gameOverCanBeRestarted() {
        val engine = BeerCatchEngine()
        engine.advance(0.05f, width = 360f, height = 640f)
        engine.moveCatcherTo(304f)

        repeat(400) {
            engine.advance(0.05f, width = 360f, height = 640f)
        }
        assertTrue(engine.state.isGameOver)

        engine.restart()

        assertEquals(0, engine.state.score)
        assertEquals(0, engine.state.misses)
        assertEquals(180f, engine.state.catcherX, 0.01f)
        assertTrue(engine.state.beers.isEmpty())
        assertTrue(!engine.state.isGameOver)
    }
}
