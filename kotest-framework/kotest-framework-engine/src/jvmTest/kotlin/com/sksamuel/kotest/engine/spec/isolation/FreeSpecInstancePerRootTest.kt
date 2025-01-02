package com.sksamuel.kotest.engine.spec.isolation

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val tests = mutableSetOf<String>()
private val specs = mutableSetOf<Int>()

class FreeSpecInstancePerRootTest : FreeSpec() {

   init {

      afterProject {
         tests.size shouldBe 5
         specs.size shouldBe 2
      }

      afterSpec {
         specs.add(it.hashCode())
      }

      afterTest {
         tests.add(it.a.name.name)
      }

      isolationMode = IsolationMode.InstancePerRoot

      val count = AtomicInteger(0)

      "a" - {
         count.incrementAndGet().shouldBe(1)
         "b" - {
            count.incrementAndGet().shouldBe(2)
            "c" {
               count.incrementAndGet().shouldBe(3)
            }
         }
      }
      "d" - {
         count.incrementAndGet().shouldBe(1)
         "e" {
            count.incrementAndGet().shouldBe(2)
         }
      }
   }
}
