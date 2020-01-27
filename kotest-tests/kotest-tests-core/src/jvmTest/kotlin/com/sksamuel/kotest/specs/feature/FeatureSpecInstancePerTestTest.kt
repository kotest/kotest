package com.sksamuel.kotest.specs.feature

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class FeatureSpecInstancePerTestTest : FeatureSpec() {
   init {
      isolation = IsolationMode.InstancePerTest

      val count = AtomicInteger(0)

      feature("A") {
         count.incrementAndGet().shouldBe(1)
         scenario("B") {
            count.incrementAndGet().shouldBe(2)
         }
         feature("C") {
            count.incrementAndGet().shouldBe(2)
            scenario("D") {
               count.incrementAndGet().shouldBe(3)
            }
            feature("E") {
               count.incrementAndGet().shouldBe(3)
               scenario("F") {
                  count.incrementAndGet().shouldBe(4)
               }
               scenario("G") {
                  count.incrementAndGet().shouldBe(4)
               }
            }
         }
      }

      feature("H") {
         count.incrementAndGet().shouldBe(1)
         feature("I") {
            count.incrementAndGet().shouldBe(2)
            scenario("J") {
               count.incrementAndGet().shouldBe(3)
            }
            scenario("K") {
               count.incrementAndGet().shouldBe(3)
            }
         }
      }
   }
}
