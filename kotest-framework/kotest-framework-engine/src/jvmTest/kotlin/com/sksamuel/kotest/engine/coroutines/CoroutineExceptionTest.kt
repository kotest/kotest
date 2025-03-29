package com.sksamuel.kotest.engine.coroutines

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.launch

/**
 * Tests that an exception in a coroutine is caught and reports the test as failed.
 */
@EnabledIf(NotMacOnGithubCondition::class)
@Isolate
class CoroutineExceptionTest : FunSpec({

   test("exception in coroutine") {

      var _result: TestResult? = null

      val listener = object : AbstractTestEngineListener() {
         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            if (testCase.name.name == "exception in coroutine") {
               _result = result
            }
         }
      }

      TestEngineLauncher(listener)
         .withClasses(FailingCoroutineTest::class)
         .launch()

      _result?.isError shouldBe true
   }
})

private class FailingCoroutineTest : FunSpec({
   test("exception in coroutine") {
      launch {
         error("boom")
      }
   }
})
