package com.sksamuel.kotest.engine.test.blocking

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class ShouldSpecBlockingTest : ShouldSpec() {
   init {

      val threads = mutableSetOf<Long>()

      context("not blocking context") {
         threads.add(Thread.currentThread().id)
         should("not blocking nested test") {
            threads.add(Thread.currentThread().id)
         }
      }

      should("not blocking root test") {
         threads.add(Thread.currentThread().id)
      }

      context("blocking context").config(blockingTest = true) {
         threads.add(Thread.currentThread().id)
         should("blocking nested test").config(blockingTest = true) {
            threads.add(Thread.currentThread().id)
         }
      }

      should("blocking root test").config(blockingTest = true) {
         threads.add(Thread.currentThread().id)
      }

      afterSpec {
         withClue(threads) {
            threads.size.shouldBe(4)
         }
      }
   }
}
