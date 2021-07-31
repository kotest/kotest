package io.kotest.engine

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.test.CallingThreadExecutionContext
import io.kotest.engine.test.RootRestrictedTestContext
import io.kotest.engine.test.TeamCityTestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.status.isEnabledInternal
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

actual class SpecRunner {

   actual fun execute(spec: Spec, onComplete: suspend () -> Unit) {
      spec.materializeAndOrderRootTests()
         .filter { it.testCase.isEnabledInternal().isEnabled }
         .forEach { runBlocking { execute(it.testCase) } }
   }

   private suspend fun execute(testCase: TestCase) = coroutineScope {
      val context = RootRestrictedTestContext(testCase, this.coroutineContext)
      TestCaseExecutor(TeamCityTestCaseExecutionListener, CallingThreadExecutionContext).execute(testCase, context)
   }
}
