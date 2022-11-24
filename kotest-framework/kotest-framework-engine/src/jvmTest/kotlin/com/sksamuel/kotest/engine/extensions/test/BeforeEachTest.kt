package com.sksamuel.kotest.engine.extensions.test

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class BeforeEachFunSpecTest : FunSpec() {
   val a = AtomicInteger(0)

   init {

      beforeEach {
         a.incrementAndGet()
      }

      context("container1") {
         withData(1,2,3){} // Increment 3 times
      }

      context("container2") {
         test("a") {} // Increment once
      }

      afterProject {
         a.get() shouldBe 4
      }
   }
}

class BeforeEachDescribeSpecTest : DescribeSpec() {
   val a = AtomicInteger(0)

   init {

      beforeEach {
         a.incrementAndGet()
      }

      describe("container1") {
         withData(1,2,3) {} // Increment 3 times
      }

      describe("container2") {
         it("a") {}// Increments once
      }

      afterProject {
         a.get() shouldBe 4
      }
   }
}
