package com.sksamuel.kotest.engine.extensions.test

import io.kotest.core.listeners.IgnoredTestListener
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.mockk.coVerify
import io.mockk.spyk

class IgnoredTestListenerTest : DescribeSpec({

   val ignoredTestListener = spyk<IgnoredTestListener>()

   register(ignoredTestListener)

   describe("should be disabled by enabled flag in config").config(enabled = false) {
      error("still enabled")
   }

   describe("!should be disabled by band") {
      error("still enabled")
   }

   xdescribe("should be disabled by xmethod") {
      error("still enabled")
   }

   describe("ignoredTestListener should be invoked") {
      coVerify(exactly = 1) {
         ignoredTestListener.ignoredTest(
            testCase = match { it.name.testName == "should be disabled by enabled flag in config" },
            reason = eq("Disabled by enabled flag in config")
         )
      }
      coVerify(exactly = 1) {
         ignoredTestListener.ignoredTest(
            testCase = match { it.name.testName == "should be disabled by band" },
            reason = eq("Disabled by bang")
         )
      }
      coVerify(exactly = 1) {
         ignoredTestListener.ignoredTest(
            testCase = match { it.name.testName == "should be disabled by xmethod" },
            reason = eq("Disabled by xmethod")
         )
      }
   }

   describe("ignoredTestListener should be invoked for ignored test by failfast strategy") {
      TestEngineLauncher()
         .withClasses(FailFastFunSpec::class)
         .withExtensions(ignoredTestListener)
         .launch()
      coVerify(exactly = 1) {
         ignoredTestListener.ignoredTest(
            testCase = match { it.name.testName == "should be ignored by failfast strategy" },
            reason = eq("Skipping test due to fail fast")
         )
      }
   }
})

private class FailFastFunSpec : FunSpec({
   context("fail fast enabled").config(failfast = true) {
      test("will be failed") {
         error("fail")
      }
      test("should be ignored by failfast strategy") {
         error("still enabled")
      }
   }
})
