package io.kotest.engine.spec.runners

import io.kotest.core.datatest.Identifiers
import io.kotest.core.internal.TestCaseExecutor
import io.kotest.core.internal.resolvedThreads
import io.kotest.core.spec.Spec
import io.kotest.core.spec.invokeAfterSpec
import io.kotest.core.spec.invokeBeforeSpec
import io.kotest.core.spec.materializeAndOrderRootTests
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseExecutionListener
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.createTestName
import io.kotest.core.test.toTestCase
import io.kotest.engine.ExecutorExecutionContext
import io.kotest.engine.launchers.TestLauncher
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecRunner
import io.kotest.engine.toTestResult
import io.kotest.fp.Try
import io.kotest.mpp.log
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [SpecRunner] that executes all tests against the
 * same [Spec] instance. In other words, only a single instance of the spec class
 * is instantiated for all the test cases.
 */
internal class SingleInstanceSpecRunner(
   listener: TestEngineListener,
   launcher: TestLauncher,
) : SpecRunner(listener, launcher) {

   private val results = ConcurrentHashMap<TestCase, TestResult>()

   override suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>> {
      log("SingleInstanceSpecRunner: executing spec [$spec]")

      suspend fun interceptAndRun(context: CoroutineContext) = Try {
         val rootTests = spec.materializeAndOrderRootTests().map { it.testCase }
         log("SingleInstanceSpecRunner: Materialized root tests: ${rootTests.size}")
         val threads = spec.resolvedThreads()
         if (threads != null && threads > 1) {
            log("Warning - usage of deprecated thread count $threads")
            runParallel(threads, rootTests) {
               log("SingleInstanceSpecRunner: Executing test $it")
               runTest(it, context)
            }
         } else {
            launch(spec) {
               log("SingleInstanceSpecRunner: Executing test $it")
               runTest(it, context)
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

   inner class Context(
      override val testCase: TestCase,
      override val coroutineContext: CoroutineContext,
   ) : TestContext {

      // these are the tests inside this context, so we can track for duplicates
      private val seen = mutableListOf<String>()

      // in the single instance runner we execute each nested test as soon as they are registered
      override suspend fun registerTestCase(nested: NestedTest) {
         log("Nested test case discovered $nested")

         val uniqueName = Identifiers.uniqueTestName(nested.name.name, seen)
         seen.add(nested.name.name)

         val nestedTestCase = nested.copy(name = createTestName(uniqueName)).toTestCase(testCase.spec, testCase)
         runTest(nestedTestCase, coroutineContext)
      }
   }

   private suspend fun runTest(
      testCase: TestCase,
      coroutineContext: CoroutineContext,
   ) {
      val testExecutor = TestCaseExecutor(object : TestCaseExecutionListener {
         override fun testStarted(testCase: TestCase) {
            listener.testStarted(testCase)
         }

         override fun testIgnored(testCase: TestCase) {
            listener.testIgnored(testCase, null)
         }

         override fun testFinished(testCase: TestCase, result: TestResult) {
            listener.testFinished(testCase, result)
         }
      }, ExecutorExecutionContext, {}, { t, duration -> toTestResult(t, duration) })

      val result = testExecutor.execute(testCase, Context(testCase, coroutineContext))
      results[testCase] = result
   }


}
