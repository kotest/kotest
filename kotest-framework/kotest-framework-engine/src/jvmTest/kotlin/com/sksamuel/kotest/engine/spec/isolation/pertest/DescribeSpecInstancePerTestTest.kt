package com.sksamuel.kotest.engine.spec.isolation.pertest

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val tests = mutableSetOf<String>()
private val specs = mutableSetOf<Int>()

class DescribeSpecInstancePerTestTest : DescribeSpec({

   afterProject {
      tests.size shouldBe 12
      specs.size shouldBe 12
   }

   afterSpec {
      specs.add(it.hashCode())
   }

   afterTest {
      tests.add(it.a.name.name)
   }

   isolationMode = IsolationMode.InstancePerTest

   val counter = AtomicInteger(0)

   describe("a") {
      counter.incrementAndGet().shouldBe(1)
      describe("b") {
         counter.incrementAndGet().shouldBe(2)
         it("c") {
            counter.incrementAndGet().shouldBe(3)
         }
         describe("d") {
            counter.incrementAndGet().shouldBe(3)
            it("e") {
               counter.incrementAndGet().shouldBe(4)
            }
         }
      }
      it("f") {
         counter.incrementAndGet().shouldBe(2)
      }
      describe("g") {
         counter.incrementAndGet().shouldBe(2)
         it("h") {
            counter.incrementAndGet().shouldBe(3)
         }
      }
   }
   describe("i") {
      counter.incrementAndGet().shouldBe(1)
      it("j") {
         counter.incrementAndGet().shouldBe(2)
      }
      describe("k") {
         counter.incrementAndGet().shouldBe(2)
         it("l") {
            counter.incrementAndGet().shouldBe(3)
         }
      }
   }

})
