package io.kotest.runner.jvm.spec

import io.kotest.core.runtime.invokeAfterSpec
import io.kotest.core.runtime.invokeBeforeSpec
import io.kotest.core.spec.Spec
import io.kotest.core.spec.materializeRootTests
import io.kotest.core.test.*
import io.kotest.fp.Try
import io.kotest.runner.jvm.TestEngineListener
import io.kotest.runner.jvm.TestExecutor
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [SpecRunner] that executes all tests against the
 * same [Spec] instance. In other words, only a single instance of the spec class
 * is instantiated for all the test cases.
 */
class SingleInstanceSpecRunner(listener: TestEngineListener) : SpecRunner(listener) {

   private val logger = LoggerFactory.getLogger(javaClass)
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
      val testExecutor = TestExecutor(listener)
      testExecutor.execute(testCase, Context(testCase, coroutineContext), true) { result ->
         results[testCase] = result
      }
   }

   override suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>> {

      suspend fun interceptAndRun(context: CoroutineContext) = Try {
         interceptSpec(spec) {
            val roots = spec.materializeRootTests()
            logger.debug("Materialized roots: $roots")
            roots.forEach { rootTest ->
               logger.trace("Executing test $rootTest")
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
