package io.kotest.engine

import io.kotest.core.spec.Spec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.teamcity.TeamCityMessageBuilder
import io.kotest.engine.test.CallingThreadExecutionContext
import io.kotest.engine.test.RootRestrictedTestContext
import io.kotest.engine.test.TeamCityTestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.status.isEnabledInternal
import kotlinx.coroutines.runBlocking

actual class SpecRunner {

   actual fun execute(spec: Spec, onComplete: suspend () -> Unit) {
      println()
      println(
         TeamCityMessageBuilder
            .testSuiteStarted(spec::class.toDescription().displayName())
            .id(spec::class.toDescription().id.value)
            .spec()
            .build()
      )
      spec.materializeAndOrderRootTests()
         .filter { it.testCase.isEnabledInternal().isEnabled }
         .forEach { execute(it.testCase) }
      println()
      println(
         TeamCityMessageBuilder
            .testSuiteFinished(spec::class.toDescription().displayName())
            .id(spec::class.toDescription().id.value)
            .spec()
            .build()
      )
   }

   private fun execute(testCase: TestCase) = runBlocking {
      val context = RootRestrictedTestContext(testCase, this.coroutineContext)
      TestCaseExecutor(TeamCityTestCaseExecutionListener, CallingThreadExecutionContext).execute(testCase, context)
   }
}
