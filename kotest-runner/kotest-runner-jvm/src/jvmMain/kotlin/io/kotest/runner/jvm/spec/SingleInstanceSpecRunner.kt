package io.kotest.runner.jvm.spec

import io.kotest.SpecClass
import io.kotest.core.Description
import io.kotest.core.TestCase
import io.kotest.core.TestContext
import io.kotest.core.TestResult
import io.kotest.core.spec.SpecConfiguration
import io.kotest.extensions.TopLevelTests
import io.kotest.runner.jvm.TestCaseExecutor
import io.kotest.runner.jvm.TestEngineListener
import org.slf4j.LoggerFactory

/**
 * Implementation of [SpecRunner] that executes all tests against the
 * same [SpecClass] instance. In other words, only a single instance of the spec class
 * is instantiated for all the test cases.
 */
class SingleInstanceSpecRunner(
   listener: TestEngineListener
) : SpecRunner(listener) {

   private val logger = LoggerFactory.getLogger(this.javaClass)
   private val executor = TestCaseExecutor(listener)
   private val results = mutableMapOf<TestCase, TestResult>()

   inner class Context(val description: Description) : TestContext() {

      // keeps track of test names we've seen so we can avoid tests trying to use the same name
      private val seen = mutableSetOf<String>()

      override fun description(): Description = description

      override suspend fun registerTestCase(testCase: TestCase) {
         // if we have a test with this name already, but the line number is different
         // then it's a duplicate test name, so boom
         if (seen.contains(testCase.name))
            throw IllegalStateException("Cannot add duplicate test name ${testCase.name}")
         seen.add(testCase.name)
         executor.execute(testCase, Context(testCase.description)) { result ->
            results[testCase] = result
         }
      }
   }

   override suspend fun execute(spec: SpecConfiguration, topLevelTests: TopLevelTests): Map<TestCase, TestResult> {
      // creating the spec instance will have invoked the init block, resulting
      // in the top level test cases being available on the spec class
      interceptSpec(spec) {
         topLevelTests.tests.forEach { topLevelTest ->
            logger.trace("Executing test $topLevelTest")
            executor.execute(
               topLevelTest.testCase,
               Context(topLevelTest.testCase.description)
            ) { result -> results[topLevelTest.testCase] = result }
         }
      }
      return results
   }
}
