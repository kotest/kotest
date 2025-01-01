package com.sksamuel.kotest.engine.extensions.test

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.listeners.IgnoredTestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.TestEngineLauncher
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class IgnoredTestListenerTest : FunSpec({

   val ignoredTests = hashSetOf<String>()

   val ignoredTestListener = object : IgnoredTestListener {
      override suspend fun ignoredTest(testCase: TestCase, reason: String?) {
         ignoredTests.add(testCase.name.name)
      }
   }

   register(ignoredTestListener)

   test("ignored listener should be fired for all combinations of ingored tests") {
      TestEngineLauncher()
         .withClasses(IgnoredTests::class)
         .withExtensions(ignoredTestListener)
         .launch()
      ignoredTests shouldBe setOf(
         "should be ignored by failfast strategy",
         "should be disabled by enabled flag in config",
         "should be disabled by xmethod",
         "!should be disabled by bang",
      )
   }
})

private class IgnoredTests : FunSpec({

   test("should be disabled by enabled flag in config").config(enabled = false) {
      error("still enabled")
   }

   test("!should be disabled by bang") {
      error("still enabled")
   }

   xtest("should be disabled by xmethod") {
      error("still enabled")
   }

   context("not ignored").config(failfast = true) {
      test("will be failed") {
         error("fail")
      }

      test("should be ignored by failfast strategy") {
         error("still enabled")
      }
   }
})
