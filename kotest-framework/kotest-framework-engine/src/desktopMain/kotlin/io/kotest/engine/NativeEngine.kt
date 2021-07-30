package io.kotest.engine

import io.kotest.core.spec.Spec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.execution.TeamCityLogger
import io.kotest.engine.preconditions.ValidateSpec
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.test.CallingThreadExecutionContext
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.status.isEnabledInternal
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

data class NativeEngineConfig(
   val validations: List<ValidateSpec>,
)

data class TestSuite(val specs: List<Spec>)

class NativeEngine(private val config: NativeEngineConfig) {
   fun execute(suite: TestSuite) {

      suite.specs.forEach { spec ->
         config.validations.forEach {
            it.invoke(spec::class)
         }
      }

      suite.specs.forEach { spec ->
         runBlocking {
            val runner = SpecRunner()
            runner.execute(spec)
         }
      }
   }
}

val listener = object : TestCaseExecutionListener {
   override fun testStarted(testCase: TestCase) {
      TeamCityLogger().start(testCase.displayName)
   }

   override fun testIgnored(testCase: TestCase) {
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      TeamCityLogger().pass(testCase.displayName, 124)
   }
}

class SpecRunner {

   suspend fun execute(spec: Spec) {
      spec.materializeAndOrderRootTests()
         .filter { it.testCase.isEnabledInternal().isEnabled }
         .forEach { execute(it.testCase) }
   }

   private suspend fun execute(testCase: TestCase) = coroutineScope {
      val context = object : TestContext {
         override val testCase: TestCase = testCase
         override val coroutineContext: CoroutineContext = this@coroutineScope.coroutineContext
         override suspend fun registerTestCase(nested: NestedTest) {
            throw IllegalStateException("Spec styles that support nested tests are disabled in kotest-native")
         }
      }
      TestCaseExecutor(listener, CallingThreadExecutionContext).execute(testCase, context)
   }
}
