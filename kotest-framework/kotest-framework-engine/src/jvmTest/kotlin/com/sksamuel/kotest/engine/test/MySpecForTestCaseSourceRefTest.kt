package com.sksamuel.kotest.engine.test

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Test case for [TestCaseSourceRefTest], defined in a separate file so that the line numbers are stable.
 */
@EnabledIf(LinuxOnlyGithubCondition::class)
internal class MySpecForTestCaseSourceRefTest : FunSpec() {
   init {
      test("my test case") {
         1 shouldBe 1
      }
      test("test case 2") {
         1 shouldBe 1
      }
   }
}
