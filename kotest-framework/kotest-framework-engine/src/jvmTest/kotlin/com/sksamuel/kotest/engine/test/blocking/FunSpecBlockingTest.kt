package com.sksamuel.kotest.engine.test.blocking

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class FunSpecBlockingTest : FunSpec() {
   init {

      val threads = mutableSetOf<Long>()

      context("not blocking context") {
         threads.add(Thread.currentThread().id)
         test("not blocking nested test") {
            threads.add(Thread.currentThread().id)
         }
      }

      test("not blocking root test") {
         threads.add(Thread.currentThread().id)
      }

      context("blocking context").config(blockingTest = true) {
         threads.add(Thread.currentThread().id)
         test("blocking nested test").config(blockingTest = true) {
            threads.add(Thread.currentThread().id)
         }
      }

      test("blocking root test").config(blockingTest = true) {
         threads.add(Thread.currentThread().id)
      }

      afterSpec {
         threads.size.shouldBe(4)
      }
   }
}
