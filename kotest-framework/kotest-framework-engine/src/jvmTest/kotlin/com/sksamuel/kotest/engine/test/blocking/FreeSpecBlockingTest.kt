package com.sksamuel.kotest.engine.test.blocking

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FreeSpecBlockingTest : FreeSpec() {
   init {

      val threads = mutableSetOf<String>()

      "not blocking context" - {
         threads.add(Thread.currentThread().name)
         "not blocking nested test" {
            threads.add(Thread.currentThread().name)
         }
      }

      "not blocking root test" {
         threads.add(Thread.currentThread().name)
      }

      "blocking context".config(blockingTest = true) - {
         threads.add(Thread.currentThread().name)
         "blocking nested test".config(blockingTest = true) {
            threads.add(Thread.currentThread().name)
         }
      }

      "blocking root test".config(blockingTest = true) {
         threads.add(Thread.currentThread().name)
      }

      afterSpec {
         threads.size.shouldBe(4)
      }
   }
}
