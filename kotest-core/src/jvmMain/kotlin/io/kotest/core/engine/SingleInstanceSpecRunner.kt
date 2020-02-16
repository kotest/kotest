package io.kotest.core.engine

import io.kotest.assertions.log
import io.kotest.core.runtime.ExecutorExecutionContext
import io.kotest.core.runtime.TestExecutionListener
import io.kotest.core.runtime.TestExecutor
import io.kotest.core.runtime.invokeAfterSpec
import io.kotest.core.runtime.invokeBeforeSpec
import io.kotest.core.spec.Spec
import io.kotest.core.spec.materializeRootTests
import io.kotest.core.test.*
import io.kotest.fp.Try
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.time.ExperimentalTime

/**
 * Implementation of [SpecRunner] that executes all tests against the
 * same [Spec] instance. In other words, only a single instance of the spec class
 * is instantiated for all the test cases.
 */
@ExperimentalTime
class SingleInstanceSpecRunner(listener: TestEngineListener) : SpecRunner(listener) {

   private val results = mutableMapOf<TestCase, TestResult>()

   inner class Context(
      override val testCase: TestCase,
      override val coroutineContext: CoroutineContext
   ) : TestContext() {

      // these are the tests inside this context, so we can track for duplicates
      private val seen = mutableSetOf<String>()

      // in the single instance runner we execute each nested test as soon as the are registered
      override suspend fun registerTestCase(nested: NestedTest) {
         val nestedTestCase = nested.toTestCase(testCase.spec, testCase.description)
         if (seen.contains(nested.name))
            throw IllegalStateException("Cannot add duplicate test name ${nested.name}")
         seen.add(nested.name)
         runTest(nestedTestCase, coroutineContext)
      }
   }

   private suspend fun runTest(
      testCase: TestCase,
      coroutineContext: CoroutineContext
   ) {
      val testExecutor = TestExecutor(object : TestExecutionListener {
         override fun testStarted(testCase: TestCase) {
            listener.testStarted(testCase)
         }

         override fun testIgnored(testCase: TestCase) {
            listener.testIgnored(testCase, null)
         }

         override fun testFinished(testCase: TestCase, result: TestResult) {
            listener.testFinished(testCase, result)
         }
      }, ExecutorExecutionContext())

      val result = testExecutor.execute(testCase, Context(testCase, coroutineContext))
      results[testCase] = result
   }

   override suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>> {

      suspend fun interceptAndRun(context: CoroutineContext) = Try {
         interceptSpec(spec) {
            val roots = spec.materializeRootTests()
            log("Materialized roots: $roots")
            roots.forEach { rootTest ->
               log("Executing test $rootTest")
               runTest(rootTest.testCase, context)
            }
         }
      }

      return coroutineScope {
         spec.invokeBeforeSpec()
            .flatMap { interceptAndRun(coroutineContext) }
            .flatMap { spec.invokeAfterSpec() }
            .map { results }
      }
   }
}
