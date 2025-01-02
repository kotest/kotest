package com.sksamuel.kotest.engine.spec.isolation.pertest

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val tests = mutableSetOf<String>()
private val specs = mutableSetOf<Int>()

class WordSpecInstancePerTestTest : WordSpec({

   afterProject {
      tests.size shouldBe 6
      specs.size shouldBe 6
   }

   afterSpec {
      specs.add(it.hashCode())
   }

   afterTest {
      tests.add(it.a.name.name)
   }

   isolationMode = IsolationMode.InstancePerTest

   val count = AtomicInteger(0)

   "a" should {
      count.incrementAndGet().shouldBe(1)
      "b" {
         count.incrementAndGet().shouldBe(2)
      }
      "c" {
         count.incrementAndGet().shouldBe(2)
      }
   }
   "d" should {
      count.incrementAndGet().shouldBe(1)
      "e" {
         count.incrementAndGet().shouldBe(2)
      }
      "f" {
         count.incrementAndGet().shouldBe(2)
      }
   }

})
