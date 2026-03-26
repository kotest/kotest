package com.sksamuel.kotest.assertions

import io.kotest.core.annotation.Issue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.time.Duration.Companion.seconds

@Suppress("KotlinConstantConditions")
@Issue("https://github.com/kotest/kotest/issues/5245")
class ShouldBePerformanceTest : FunSpec() {
   init {

      test("shouldBe performance").config(timeout = 2.seconds) {
         (1..1_000_000).forEach {
            (it % 1) shouldBe 0
         }
      }

      test("shouldNotBe should not take longer to execute than shouldBe").config(timeout = 2.seconds) {
         (1..1_000_000).forEach {
            it shouldNotBe 0
         }
      }
   }
}
