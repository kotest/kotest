package io.kotest.engine

import io.kotest.core.config.configuration
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.engine.interceptors.EmptyTestSuiteInterceptor
import io.kotest.engine.interceptors.EngineInterceptor
import io.kotest.engine.interceptors.ProjectExtensionEngineInterceptor
import io.kotest.engine.interceptors.ProjectListenerEngineInterceptor
import io.kotest.engine.interceptors.SpecSortEngineInterceptor
import io.kotest.engine.interceptors.SpecStyleValidationInterceptor
import io.kotest.engine.interceptors.TestDslStateInterceptor
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.teamcity.TeamCityMessageBuilder
import io.kotest.engine.test.CallingThreadExecutionContext
import io.kotest.engine.test.RootRestrictedTestContext
import io.kotest.engine.test.listener.TeamCityTestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.status.isEnabledInternal
import io.kotest.mpp.log
import kotlinx.coroutines.runBlocking

actual class SpecRunner {

   actual fun execute(spec: Spec, onComplete: suspend () -> Unit) {
      log { "Executing spec $spec" }
      println()
      println(
         TeamCityMessageBuilder
            .testSuiteStarted(spec::class.toDescription().displayName())
            .id(spec::class.toDescription().id.value)
            .spec()
            .build()
      )
      println()
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
      println()
      runBlocking {
         onComplete()
      }
   }

   private fun execute(testCase: TestCase) = runBlocking {
      log { "Executing testCase $testCase" }
      val context = RootRestrictedTestContext(testCase, this.coroutineContext)
      TestCaseExecutor(TeamCityTestCaseExecutionListener, CallingThreadExecutionContext)
         .execute(testCase, context)
   }
}

actual fun testEngineInterceptors(): List<EngineInterceptor> {
   return listOfNotNull(
      TestDslStateInterceptor,
      SpecStyleValidationInterceptor,
      SpecSortEngineInterceptor,
      ProjectExtensionEngineInterceptor(configuration.extensions().filterIsInstance<ProjectExtension>()),
      ProjectListenerEngineInterceptor(configuration.extensions()),
      if (configuration.failOnEmptyTestSuite) EmptyTestSuiteInterceptor else null,
   )
}
