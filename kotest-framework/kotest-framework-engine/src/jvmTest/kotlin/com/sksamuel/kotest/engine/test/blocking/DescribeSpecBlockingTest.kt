package com.sksamuel.kotest.engine.test.blocking

import io.kotest.assertions.withClue
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class DescribeSpecBlockingTest : DescribeSpec() {
   init {

      val threads = mutableSetOf<Long>()

      context("not blocking context") {
         threads.add(Thread.currentThread().id)
         describe("not blocking nested test") {
            threads.add(Thread.currentThread().id)
         }
      }

      describe("not blocking root test") {
         threads.add(Thread.currentThread().id)
      }

      context("blocking context").config(blockingTest = true) {
         threads.add(Thread.currentThread().id)
         describe("blocking nested test").config(blockingTest = true) {
            threads.add(Thread.currentThread().id)
         }
      }

      describe("blocking root test").config(blockingTest = true) {
         threads.add(Thread.currentThread().id)
      }

      afterSpec {
         withClue(threads) {
            threads.size.shouldBe(4)
         }
      }
   }
}
