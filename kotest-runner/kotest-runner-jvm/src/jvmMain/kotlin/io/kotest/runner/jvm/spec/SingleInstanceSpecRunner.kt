package io.kotest.runner.jvm.spec

import io.kotest.SpecClass
import io.kotest.core.Description
import io.kotest.core.TestCase
import io.kotest.core.TestContext
import io.kotest.core.TestResult
import io.kotest.core.spec.SpecConfiguration
import io.kotest.extensions.TopLevelTest
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
class SingleInstanceSpecRunner(
   listener: TestEngineListener
) : SpecRunner(listener) {

   private val logger = LoggerFactory.getLogger(this.javaClass)
   private val executor = TestExecutor(listener)
   private val results = mutableMapOf<TestCase, TestResult>()

   inner class Context(
      val spec: SpecConfiguration,
      val description: Description,
      override val coroutineContext: CoroutineContext
   ) : TestContext() {

      // keeps track of test names we've seen so we can avoid tests trying to use the same name
      private val seen = mutableSetOf<String>()

      override fun description(): Description = description
      override fun spec(): SpecConfiguration = spec

      override suspend fun registerTestCase(testCase: TestCase) {
         // if we have a test with this name already, but the line number is different
         // then it's a duplicate test name, so boom
         if (seen.contains(testCase.name))
            throw IllegalStateException("Cannot add duplicate test name ${testCase.name}")
         seen.add(testCase.name)
         executor.execute(testCase, Context(testCase.spec, testCase.description, coroutineContext)) { result ->
            results[testCase] = result
         }
      }
   }

   override suspend fun execute(spec: SpecConfiguration, topLevelTests: List<TopLevelTest>): Map<TestCase, TestResult> {
      // creating the spec instance will have invoked the init block, resulting
      // in the top level test cases being available on the spec class
      coroutineScope {
         interceptSpec(spec) {
            topLevelTests.forEach { topLevelTest ->
               logger.trace("Executing test $topLevelTest")
               executor.execute(
                  topLevelTest.testCase,
                  Context(topLevelTest.testCase.spec, topLevelTest.testCase.description, coroutineContext)
               ) { result -> results[topLevelTest.testCase] = result }
            }
         }
      }
      return results
   }
}
