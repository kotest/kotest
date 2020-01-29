package com.sksamuel.kotest.specs.isolation.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val tests = mutableSetOf<String>()
private val specs = mutableSetOf<Int>()

class FreeSpecInstancePerTestTest : FreeSpec({

   afterProject {
      tests.size shouldBe 7
      specs.size shouldBe 7
   }

   afterSpec {
      specs.add(it.hashCode())
   }

   afterTest {
      tests.add(it.a.name)
   }

   isolation = IsolationMode.InstancePerTest

   val count = AtomicInteger(0)


   "a" - {
      count.incrementAndGet().shouldBe(1)
      "b" - {
         count.incrementAndGet().shouldBe(2)
         "c".config() {
            count.incrementAndGet().shouldBe(3)
         }
      }
      "d" - {
         count.incrementAndGet().shouldBe(2)
         "e" {
            count.incrementAndGet().shouldBe(3)
         }
      }
   }
   "f" - {
      count.incrementAndGet().shouldBe(1)
      "g" {
         count.incrementAndGet().shouldBe(2)
      }
   }

})
