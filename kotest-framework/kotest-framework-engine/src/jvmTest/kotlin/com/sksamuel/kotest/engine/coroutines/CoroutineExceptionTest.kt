package com.sksamuel.kotest.engine.coroutines

import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.launch

/**
 * Tests that an exception in a coroutine is caught and reports the test as failed.
 */
@Isolate
class CoroutineExceptionTest : FunSpec({

   test("exception in coroutine") {
      var _result: TestResult? = null
      val listener = object : TestEngineListener {
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            if (testCase.name.testName == "exception in coroutine") {
               _result = result
            }
         }
      }
      KotestEngineLauncher()
         .withListener(listener)
         .withSpec(FailingCoroutineTest::class)
         .launch()
      _result?.status shouldBe TestStatus.Error
   }
})

private class FailingCoroutineTest : FunSpec({
   test("exception in coroutine") {
      launch {
         error("boom")
      }
   }
})
