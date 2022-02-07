package com.sksamuel.kotest.engine.test.blocking

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DescribeSpecBlockingTest : DescribeSpec() {
   init {

      val threads = mutableSetOf<String>()

      context("not blocking context") {
         threads.add(Thread.currentThread().name)
         describe("not blocking nested test") {
            threads.add(Thread.currentThread().name)
         }
      }

      describe("not blocking root test") {
         threads.add(Thread.currentThread().name)
      }

      context("blocking context").config(blockingTest = true) {
         threads.add(Thread.currentThread().name)
         describe("blocking nested test").config(blockingTest = true) {
            threads.add(Thread.currentThread().name)
         }
      }

      describe("blocking root test").config(blockingTest = true) {
         threads.add(Thread.currentThread().name)
      }

      afterSpec {
         withClue(threads) {
            threads.size.shouldBe(4)
         }
      }
   }
}
