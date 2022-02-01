package com.sksamuel.kotest.engine.test.blocking

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class FeatureSpecBlockingTest : FeatureSpec() {
   init {

      val threads = mutableSetOf<String>()

      feature("not blocking context") {
         threads.add(Thread.currentThread().name)
         scenario("not blocking nested test") {
            threads.add(Thread.currentThread().name)
         }
      }

      feature("blocking context").config(blockingTest = true) {
         threads.add(Thread.currentThread().name)
         scenario("blocking nested test").config(blockingTest = true) {
            threads.add(Thread.currentThread().name)
         }
      }

      afterSpec {
         threads.size.shouldBe(3)
      }
   }
}
