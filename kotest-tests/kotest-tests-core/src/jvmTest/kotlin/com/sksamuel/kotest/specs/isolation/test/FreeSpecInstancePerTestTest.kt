package com.sksamuel.kotest.specs.isolation.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val tests = mutableSetOf<String>()
private val specs = mutableSetOf<Int>()

class FreeSpecInstancePerTestTest : FreeSpec({

   afterProject {
      tests.size shouldBe 9
      specs.size shouldBe 9
   }

   afterSpec {
      specs.add(it.hashCode())
   }

   afterTest {
      tests.add(it.a.displayName)
   }

   isolationMode = IsolationMode.InstancePerTest

   val count = AtomicInteger(0)

   "a" - {
      count.incrementAndGet().shouldBe(1)
      "b" - {
         count.incrementAndGet().shouldBe(2)
         "c".config {
            count.incrementAndGet().shouldBe(3)
         }
         "d".config {
            count.incrementAndGet().shouldBe(3)
         }
      }
      "e" - {
         count.incrementAndGet().shouldBe(2)
         "f" {
            count.incrementAndGet().shouldBe(3)
         }
      }
   }
   "g" - {
      count.incrementAndGet().shouldBe(1)
      "h" {
         count.incrementAndGet().shouldBe(2)
      }
      "i" {
         count.incrementAndGet().shouldBe(2)
      }
   }

})
