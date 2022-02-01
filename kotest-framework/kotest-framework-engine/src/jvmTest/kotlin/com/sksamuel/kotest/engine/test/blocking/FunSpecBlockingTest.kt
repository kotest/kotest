package com.sksamuel.kotest.engine.test.blocking

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunSpecBlockingTest : FunSpec() {
   init {

      val threads = mutableSetOf<String>()

      context("not blocking context") {
         threads.add(Thread.currentThread().name)
         test("not blocking nested test") {
            threads.add(Thread.currentThread().name)
         }
      }

      test("not blocking root test") {
         threads.add(Thread.currentThread().name)
      }

      context("blocking context").config(blockingTest = true) {
         threads.add(Thread.currentThread().name)
         test("blocking nested test").config(blockingTest = true) {
            threads.add(Thread.currentThread().name)
         }
      }

      test("blocking root test").config(blockingTest = true) {
         threads.add(Thread.currentThread().name)
      }

      afterSpec {
         threads.size.shouldBe(4)
      }
   }
}
