package com.sksamuel.kotest.engine

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

// controls if the failing test is included
// need this so when the overall test engine runs, the failing test inside PrivateClassesAreIgnoredTest is not included
private var includeTest = false

/**
 * Tests that private classes can be ignored when the option is set
 */
@EnabledIf(NotMacOnGithubCondition::class)
class PrivateClassesIngoreOptionTest : FunSpec() {
   init {
      test("private class should be ignore") {
         includeTest = true // causes the error test to be included when this engine runs
         val p = object : AbstractProjectConfig() {
            override val ignorePrivateClasses = true
         }
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(PrivateClassesAreIgnoredTest::class)
            .withProjectConfig(p)
            .launch()
         listener.errors shouldBe false
         listener.tests.size shouldBe 0
         includeTest = false
      }
   }
}

private class PrivateClassesAreIgnoredTest : FunSpec() {
   init {
      if (includeTest)
         test("should not be invoked") {
            error("boom")
         }
   }
}
