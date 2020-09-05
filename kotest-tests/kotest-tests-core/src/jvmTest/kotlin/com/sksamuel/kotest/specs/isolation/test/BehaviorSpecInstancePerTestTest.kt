package com.sksamuel.kotest.specs.isolation.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val tests = mutableSetOf<String>()
private val specs = mutableSetOf<Int>()

class BehaviorSpecInstancePerTestTest : BehaviorSpec({

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

   Given("a") {
      count.incrementAndGet().shouldBe(1)
      When("b") {
         count.incrementAndGet().shouldBe(2)
      }
      When("c") {
         count.incrementAndGet().shouldBe(2)
         Then("d") {
            count.incrementAndGet().shouldBe(3)
         }
         Then("e") {
            count.incrementAndGet().shouldBe(3)
         }
      }
   }
   Given("f") {
      count.incrementAndGet().shouldBe(1)
      When("g") {
         count.incrementAndGet().shouldBe(2)
         Then("h") {
            count.incrementAndGet().shouldBe(3)
         }
         Then("i") {
            count.incrementAndGet().shouldBe(3)
         }
      }
   }
})
