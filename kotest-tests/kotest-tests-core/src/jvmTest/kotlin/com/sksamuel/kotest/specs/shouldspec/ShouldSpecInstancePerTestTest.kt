package com.sksamuel.kotest.specs.shouldspec

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class ShouldSpecInstancePerTestTest : ShouldSpec() {

   override fun isolationMode() = IsolationMode.InstancePerTest

   init {

      val counter = AtomicInteger(0)

      "1" {
         counter.incrementAndGet().shouldBe(1)
         should("1.1") {
            counter.incrementAndGet().shouldBe(2)
         }
         "1.2" {
            counter.incrementAndGet().shouldBe(2)
            should("1.2.1") {
               counter.incrementAndGet().shouldBe(3)
            }
            "1.2.2" {
               counter.incrementAndGet().shouldBe(3)
               should("1.2.2.1") {
                  counter.incrementAndGet().shouldBe(4)
               }
               should("1.2.2.2") {
                  counter.incrementAndGet().shouldBe(4)
               }
            }
         }
      }
      "2" {
         counter.incrementAndGet().shouldBe(1)
         "2.1" {
            counter.incrementAndGet().shouldBe(2)
            should("2.1.1") {
               counter.incrementAndGet().shouldBe(3)
            }
            should("2.1.2") {
               counter.incrementAndGet().shouldBe(3)
            }
         }
      }
   }
}
