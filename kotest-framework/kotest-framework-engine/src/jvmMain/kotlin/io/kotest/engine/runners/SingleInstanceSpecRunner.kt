package io.kotest.engine.runners

import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.spec.Spec
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.toTestCase
import io.kotest.engine.spec.SpecRunner
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.internal.resolvedThreads
import io.kotest.engine.ExecutorExecutionContext
import io.kotest.core.internal.TestCaseExecutor
import io.kotest.core.spec.invokeAfterSpec
import io.kotest.core.spec.invokeBeforeSpec
import io.kotest.core.spec.materializeAndOrderRootTests
import io.kotest.core.test.TestCaseExecutionListener
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
internal class SingleInstanceSpecRunner(listener: TestEngineListener) : SpecRunner(listener) {

   private val results = ConcurrentHashMap<TestCase, TestResult>()

   inner class Context(
      override val testCase: TestCase,
      override val coroutineContext: CoroutineContext,
   ) : TestContext {

      // these are the tests inside this context, so we can track for duplicates
      private val seen = mutableSetOf<DescriptionName.TestName>()

      // in the single instance runner we execute each nested test as soon as the are registered
      override suspend fun registerTestCase(nested: NestedTest) {
         log("Nested test case discovered $nested")
         val nestedTestCase = nested.toTestCase(testCase.spec, testCase.description)
         if (seen.contains(nested.name))
            throw DuplicatedTestNameException(nested.name)
         seen.add(nested.name)
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

   override suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>> {

      suspend fun interceptAndRun(context: CoroutineContext) = Try {
         val rootTests = spec.materializeAndOrderRootTests().map { it.testCase }
         runParallel(spec.resolvedThreads(), rootTests) {
            log("Executing test $it")
            runTest(it, context)
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
