package com.sksamuel.kotest.engine.test.blocking

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe

class ExpectSpecBlockingTest : ExpectSpec() {
   init {

      val threads = mutableSetOf<String>()

      context("not blocking context") {
         threads.add(Thread.currentThread().name)
         expect("not blocking nested test") {
            threads.add(Thread.currentThread().name)
         }
      }

      expect("not blocking root test") {
         threads.add(Thread.currentThread().name)
      }

      context("blocking context").config(blockingTest = true) {
         threads.add(Thread.currentThread().name)
         expect("blocking nested test").config(blockingTest = true) {
            threads.add(Thread.currentThread().name)
         }
      }

      expect("blocking root test").config(blockingTest = true) {
         threads.add(Thread.currentThread().name)
      }

      afterSpec {
         threads.size.shouldBe(4)
      }
   }
}
