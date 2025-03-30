package com.sksamuel.kotest.engine.test.blocking

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe

@EnabledIf(NotMacOnGithubCondition::class)
class ExpectSpecBlockingTest : ExpectSpec() {
   init {

      val threads = mutableSetOf<Long>()

      context("not blocking context") {
         threads.add(Thread.currentThread().id)
         expect("not blocking nested test") {
            threads.add(Thread.currentThread().id)
         }
      }

      expect("not blocking root test") {
         threads.add(Thread.currentThread().id)
      }

      context("blocking context").config(blockingTest = true) {
         threads.add(Thread.currentThread().id)
         expect("blocking nested test").config(blockingTest = true) {
            threads.add(Thread.currentThread().id)
         }
      }

      expect("blocking root test").config(blockingTest = true) {
         threads.add(Thread.currentThread().id)
      }

      afterSpec {
         threads.size.shouldBe(4)
      }
   }
}
