package io.kotest.matchers.kotlinx.datetime

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.milliseconds

class InstantTests : FreeSpec({

    "same instance of instant should be same" {
        val currentInstant = Clock.System.now()
        currentInstant shouldBe currentInstant
    }

    "different instance of instant having same time should be same" {
        Instant.fromEpochMilliseconds(10000) shouldBe Instant.fromEpochMilliseconds(10000)
    }

    "instance of different time should be different" {
        val currentInstant = Clock.System.now()
        val pastInstant = currentInstant.minus(40000.milliseconds)
        currentInstant shouldNotBe pastInstant
    }

    "past instant should be before current instant" {
        val currentInstant = Clock.System.now()
        val pastInstant = currentInstant.minus(1000.milliseconds)

        pastInstant shouldBeBefore currentInstant
    }

    "current instant should not be before past instant" {
        val currentInstant = Clock.System.now()
        val pastInstant = currentInstant.minus(1000.milliseconds)

        currentInstant shouldNotBeBefore pastInstant
    }

    "future instant should be after current instant" {
        val currentInstant = Clock.System.now()
        val futureInstant = currentInstant.plus(1000.milliseconds)

        futureInstant shouldBeAfter currentInstant
    }

    "current instant should not be after past instant" {
        val currentInstant = Clock.System.now()
        val futureInstant = currentInstant.plus(1000.milliseconds)

        currentInstant shouldNotBeAfter futureInstant
    }

    "instant of same time should not be before another instant of same time" {
        Instant.fromEpochMilliseconds(30000) shouldNotBeBefore Instant.fromEpochMilliseconds(30000)
    }

    "instant of same time should not be after another instant of same time" {
        Instant.fromEpochMilliseconds(30000) shouldNotBeAfter Instant.fromEpochMilliseconds(30000)
    }

    "current instant should be between past instant and future instant" {
        val currentInstant = Clock.System.now()
        val pastInstant = currentInstant.minus(30000.milliseconds)
        val futureInstant = currentInstant.plus(30000.milliseconds)

        currentInstant.shouldBeBetween(pastInstant, futureInstant)
    }

    "past instant should not be between current instant and future instant" {
        val currentInstant = Clock.System.now()
        val pastInstant = currentInstant.minus(30000.milliseconds)
        val futureInstant = currentInstant.plus(30000.milliseconds)

        pastInstant.shouldNotBeBetween(currentInstant, futureInstant)
    }

    "future instant should not be between past instant and current instant" {
        val currentInstant = Clock.System.now()
        val pastInstant = currentInstant.minus(30000.milliseconds)
        val futureInstant = currentInstant.plus(30000.milliseconds)

        futureInstant.shouldNotBeBetween(pastInstant, currentInstant)
    }

})
