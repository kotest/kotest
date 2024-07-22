package com.sksamuel.kotest.engine.extensions.test

import io.kotest.core.listeners.DisabledTestListener
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.coVerify
import io.mockk.spyk

class DisabledTestListenerTest : DescribeSpec({

   val disabledTestListener = spyk<DisabledTestListener>()

   register(disabledTestListener)

   describe("should be disabled by enabled flag in config").config(enabled = false) {
      throw IllegalStateException("still enabled")
   }

   describe("!should be disabled by band") {
      throw IllegalStateException("still enabled")
   }

   xdescribe("should be disabled by xmethod") {
      throw IllegalStateException("still enabled")
   }

   describe("disabledTestListener should be invoked") {
      coVerify(exactly = 1) {
         disabledTestListener.disabledTest(
            testCase = match { it.name.testName == "should be disabled by enabled flag in config" },
            reason = eq("Disabled by enabled flag in config")
         )
      }
      coVerify(exactly = 1) {
         disabledTestListener.disabledTest(
            testCase = match { it.name.testName == "should be disabled by band" },
            reason = eq("Disabled by bang")
         )
      }
      coVerify(exactly = 1) {
         disabledTestListener.disabledTest(
            testCase = match { it.name.testName == "should be disabled by xmethod" },
            reason = eq("Disabled by xmethod")
         )
      }
   }
})
