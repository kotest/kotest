package com.sksamuel.kotest.engine.spec.isolation

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val tests = mutableSetOf<String>()
private val specs = mutableSetOf<Int>()

class FeatureSpecInstancePerRootTest : FeatureSpec({

   afterProject {
      tests.size shouldBe 11
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

   feature("A") {
      count.incrementAndGet().shouldBe(1)
      scenario("B") {
         count.incrementAndGet().shouldBe(2)
      }
      feature("C") {
         count.incrementAndGet().shouldBe(3)
         scenario("D") {
            count.incrementAndGet().shouldBe(4)
         }
         feature("E") {
            count.incrementAndGet().shouldBe(5)
            scenario("F") {
               count.incrementAndGet().shouldBe(6)
            }
            scenario("G") {
               count.incrementAndGet().shouldBe(7)
            }
         }
      }
   }

   feature("H") {
      count.incrementAndGet().shouldBe(1)
      feature("I") {
         count.incrementAndGet().shouldBe(2)
         scenario("J") {
            count.incrementAndGet().shouldBe(3)
         }
         scenario("K") {
            count.incrementAndGet().shouldBe(4)
         }
      }
   }

})
