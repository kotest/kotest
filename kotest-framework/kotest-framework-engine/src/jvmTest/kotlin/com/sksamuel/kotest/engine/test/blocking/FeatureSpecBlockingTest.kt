package com.sksamuel.kotest.engine.test.blocking

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

@EnabledIf(NotMacOnGithubCondition::class)
class FeatureSpecBlockingTest : FeatureSpec() {
   init {

      val threads = mutableSetOf<Long>()

      feature("not blocking context") {
         threads.add(Thread.currentThread().id)
         scenario("not blocking nested test") {
            threads.add(Thread.currentThread().id)
         }
      }

      feature("blocking context").config(blockingTest = true) {
         threads.add(Thread.currentThread().id)
         scenario("blocking nested test").config(blockingTest = true) {
            threads.add(Thread.currentThread().id)
         }
      }

      afterSpec {
         threads.size.shouldBe(3)
      }
   }
}
