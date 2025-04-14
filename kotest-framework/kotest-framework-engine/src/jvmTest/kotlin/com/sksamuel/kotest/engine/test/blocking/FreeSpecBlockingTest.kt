package com.sksamuel.kotest.engine.test.blocking

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class FreeSpecBlockingTest : FreeSpec() {
   init {

      val threads = mutableSetOf<Long>()

      "not blocking context" - {
         threads.add(Thread.currentThread().id)
         "not blocking nested test" {
            threads.add(Thread.currentThread().id)
         }
      }

      "not blocking root test" {
         threads.add(Thread.currentThread().id)
      }

      "blocking context".config(blockingTest = true) - {
         threads.add(Thread.currentThread().id)
         "blocking nested test".config(blockingTest = true) {
            threads.add(Thread.currentThread().id)
         }
      }

      "blocking root test".config(blockingTest = true) {
         threads.add(Thread.currentThread().id)
      }

      afterSpec {
         threads.size.shouldBe(4)
      }
   }
}
