package com.sksamuel.kotest.engine.spec.isolation

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val tests = mutableSetOf<String>()
private val specs = mutableSetOf<Int>()

class BehaviorSpecInstancePerRootTest : BehaviorSpec({

   afterProject {
      tests.size shouldBe 10
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

   Given("a") {
      count.incrementAndGet().shouldBe(1)
      When("b") {
         count.incrementAndGet().shouldBe(2)
         Then("c") {
            count.incrementAndGet().shouldBe(3)
         }
      }
      When("d") {
         count.incrementAndGet().shouldBe(4)
         Then("e") {
            count.incrementAndGet().shouldBe(5)
         }
         Then("f") {
            count.incrementAndGet().shouldBe(6)
         }
      }
   }
   Given("g") {
      count.incrementAndGet().shouldBe(1)
      When("h") {
         count.incrementAndGet().shouldBe(2)
         Then("i") {
            count.incrementAndGet().shouldBe(3)
         }
         Then("j") {
            count.incrementAndGet().shouldBe(4)
         }
      }
   }
})
