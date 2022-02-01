package com.sksamuel.kotest.engine.test.blocking

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class ShouldSpecBlockingTest : ShouldSpec() {
   init {

      val threads = mutableSetOf<String>()

      context("not blocking context") {
         threads.add(Thread.currentThread().name)
         should("not blocking nested test") {
            threads.add(Thread.currentThread().name)
         }
      }

      should("not blocking root test") {
         threads.add(Thread.currentThread().name)
      }

      context("blocking context").config(blockingTest = true) {
         threads.add(Thread.currentThread().name)
         should("blocking nested test").config(blockingTest = true) {
            threads.add(Thread.currentThread().name)
         }
      }

      should("blocking root test").config(blockingTest = true) {
         threads.add(Thread.currentThread().name)
      }

      afterSpec {
         threads.size.shouldBe(4)
      }
   }
}
