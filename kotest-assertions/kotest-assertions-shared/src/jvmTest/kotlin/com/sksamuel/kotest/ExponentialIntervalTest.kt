package com.sksamuel.kotest

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.until.exponential
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.hours
import kotlin.time.seconds

class ExponentialIntervalTest : FunSpec() {
   init {
      test("exp cap correctness") {
         val cap = 278.hours
         val unbounded = 1.seconds.exponential()
         val bounded = 1.seconds.exponential(cap = cap)

         assertSoftly {
            for (i in 0..20) {
               val u = unbounded.next(i)
               val b = bounded.next(i)
               if (u < cap) {
                  withClue("durations under the cap should be unchanged") {
                     b shouldBe u
                  }
               } else {
                  withClue("durations over the cap should be clamped") {
                     b shouldBe cap
                  }
               }
            }
         }
      }
   }
}
