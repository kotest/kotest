package io.kotest.extensions.clock

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

class TestClockTest : FunSpec({

   test("clock should be mutable") {
      val clock = TestClock.utc(Instant.ofEpochMilli(100))
      clock.instant() shouldBe Instant.ofEpochMilli(100)
      clock.plus(10.seconds)
      clock.instant() shouldBe Instant.ofEpochMilli(10100)
   }

})
