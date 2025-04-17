package com.sksamuel.kotest.engine.spec.isolation

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val tests = mutableSetOf<String>()
private val specs = mutableSetOf<Int>()

class ExpectSpecInstancePerRootTest : ExpectSpec({

   afterProject {
      tests.size shouldBe 12
      specs.size shouldBe 2
   }

   afterSpec {
      specs.add(it.hashCode())
   }

   afterTest {
      tests.add(it.a.name.name)
   }

   isolationMode = IsolationMode.InstancePerRoot

   val counter = AtomicInteger(0)

   context("a") {
      counter.incrementAndGet().shouldBe(1)
      context("b") {
         counter.incrementAndGet().shouldBe(2)
         expect("c") {
            counter.incrementAndGet().shouldBe(3)
         }
         context("d") {
            counter.incrementAndGet().shouldBe(4)
            expect("e") {
               counter.incrementAndGet().shouldBe(5)
            }
         }
      }
      expect("f") {
         counter.incrementAndGet().shouldBe(6)
      }
      context("g") {
         counter.incrementAndGet().shouldBe(7)
         expect("h") {
            counter.incrementAndGet().shouldBe(8)
         }
      }
   }
   context("i") {
      counter.incrementAndGet().shouldBe(1)
      expect("j") {
         counter.incrementAndGet().shouldBe(2)
      }
      context("k") {
         counter.incrementAndGet().shouldBe(3)
         expect("l") {
            counter.incrementAndGet().shouldBe(4)
         }
      }
   }

})
