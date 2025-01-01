package com.sksamuel.kotest.engine.spec.isolation.pertest

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val tests = mutableSetOf<String>()
private val specs = mutableSetOf<Int>()

class ShouldSpecInstancePerTestTest : ShouldSpec({

   afterProject {
      tests.size shouldBe 11
      specs.size shouldBe 11
   }

   afterSpec {
      specs.add(it.hashCode())
   }

   afterTest {
      tests.add(it.a.name.name)
   }

   isolationMode = IsolationMode.InstancePerTest
   val counter = AtomicInteger(0)

   context("a") {
      counter.incrementAndGet().shouldBe(1)
      should("b") {
         counter.incrementAndGet().shouldBe(2)
      }
      context("c") {
         counter.incrementAndGet().shouldBe(2)
         should("d") {
            counter.incrementAndGet().shouldBe(3)
         }
         context("e") {
            counter.incrementAndGet().shouldBe(3)
            should("f") {
               counter.incrementAndGet().shouldBe(4)
            }
            should("g") {
               counter.incrementAndGet().shouldBe(4)
            }
         }
      }
   }
   context("h") {
      counter.incrementAndGet().shouldBe(1)
      context("i") {
         counter.incrementAndGet().shouldBe(2)
         should("j") {
            counter.incrementAndGet().shouldBe(3)
         }
         should("k") {
            counter.incrementAndGet().shouldBe(3)
         }
      }
   }
})
