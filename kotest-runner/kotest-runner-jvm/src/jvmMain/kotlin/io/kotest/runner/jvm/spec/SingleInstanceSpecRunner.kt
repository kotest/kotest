package io.kotest.runner.jvm.spec

import io.kotest.core.SpecClass
import io.kotest.core.spec.SpecConfiguration
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
 * same [SpecClass] instance. In other words, only a single instance of the spec class
 * is instantiated for all the test cases.
 */
class SingleInstanceSpecRunner(listener: TestEngineListener) : SpecRunner(listener) {

   private val logger = LoggerFactory.getLogger(javaClass)
   private val executor = TestExecutor(listener)
   private val results = mutableMapOf<TestCase, TestResult>()

   // keeps track of full test names so we can avoid tests trying to use the same name
   private val seen = mutableSetOf<Description>()

   inner class Context(
      override val testCase: TestCase,
      override val coroutineContext: CoroutineContext
   ) : TestContext() {
      // in the single instance runner we execute each nested test as soon as the are registered
      override suspend fun registerTestCase(test: NestedTest) {
         val nestedTestCase = test.toTestCase(testCase.spec, testCase.description)
         if (seen.contains(nestedTestCase.description))
            throw IllegalStateException("Cannot add duplicate test name ${test.name}")
         executor.execute(testCase, Context(nestedTestCase, coroutineContext)) { result ->
            results[testCase] = result
         }
      }
   }

   override suspend fun execute(spec: SpecConfiguration): Try<Map<TestCase, TestResult>> {
      return coroutineScope {
         notifyBeforeSpec(spec)
            .flatMap { spec ->
               interceptSpec(spec) {
                  spec.materializeRootTests().forEach { rootTest ->
                     logger.trace("Executing test $rootTest")
                     executor.execute(
                        rootTest.testCase,
                        Context(rootTest.testCase, coroutineContext)
                     ) { result -> results[rootTest.testCase] = result }
                  }
               }
            }
            .flatMap { notifyAfterSpec(it) }
      }.map { results }
   }
}
