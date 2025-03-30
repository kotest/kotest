package com.sksamuel.kotest

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.Order
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

// this is used to generate some data for the xml report
@Order(0)
@EnabledIf(LinuxOnlyGithubCondition::class)
class DummyFunSpecTest : FunSpec() {
   init {
      context("context") {
         test("a") {
            1 + 1 shouldBe 2
         }
         test("b") {
            1 + 1 shouldBe 2
         }
      }
   }
}
