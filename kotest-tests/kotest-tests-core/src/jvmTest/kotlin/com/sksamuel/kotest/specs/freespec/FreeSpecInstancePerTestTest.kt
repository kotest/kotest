package com.sksamuel.kotest.specs.freespec

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class FreeSpecInstancePerTestTest : FreeSpec() {

   override fun isolationMode() = IsolationMode.InstancePerTest

   val counter = AtomicInteger(0)

   init {
      "a" - {
         counter.incrementAndGet().shouldBe(1)
         "b" - {
            counter.incrementAndGet().shouldBe(2)
            "c".config() {
               counter.incrementAndGet().shouldBe(3)
            }
         }
         counter.incrementAndGet().shouldBe(2)
         "d" - {
            counter.incrementAndGet().shouldBe(3)
            "e" {
               counter.incrementAndGet().shouldBe(4)
            }
         }
      }
      "f" - {
         counter.incrementAndGet().shouldBe(1)
         "g" {
            counter.incrementAndGet().shouldBe(2)
         }
      }
   }
}
