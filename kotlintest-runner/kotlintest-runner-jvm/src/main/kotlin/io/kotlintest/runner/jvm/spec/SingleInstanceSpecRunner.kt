package io.kotlintest.runner.jvm.spec

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestContext
import io.kotlintest.TestResult
import io.kotlintest.extensions.TopLevelTest
import io.kotlintest.internal.isActive
import io.kotlintest.runner.jvm.TestCaseExecutor
import io.kotlintest.runner.jvm.TestEngineListener
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [SpecRunner] that executes all tests against the
 * same [Spec] instance. In other words, only a single instance of the spec class
 * is instantiated for all the test cases.
 */
class SingleInstanceSpecRunner(listener: TestEngineListener,
                               listenerExecutor: ExecutorService,
                               scheduler: ScheduledExecutorService) : SpecRunner(listener) {

  private val logger = LoggerFactory.getLogger(this.javaClass)
  private val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)
  private val results = mutableMapOf<TestCase, TestResult>()

  inner class Context(val description: Description, coroutineContext: CoroutineContext) : TestContext(coroutineContext) {

    // test names mapped to their line numbers, allows detection of duplicate test names
    // the line number is required because the same test is allowed to be invoked multiple times
    private val seen = HashMap<String, Int>()

    override fun description(): Description = description

    override suspend fun registerTestCase(testCase: TestCase) {
      // if we have a test with this name already, but the line number is different
      // then it's a duplicate test name, so boom
      if (seen.containsKey(testCase.name) && seen[testCase.name] != testCase.line)
        throw IllegalStateException("Cannot add duplicate test name ${testCase.name}")
      seen[testCase.name] = testCase.line
      executor.execute(testCase, isActive(testCase), Context(testCase.description, coroutineContext)) { result -> results[testCase] = result }
    }
  }

  override fun execute(spec: Spec, topLevelTests: List<TopLevelTest>): Map<TestCase, TestResult> {

    // creating the spec instance will have invoked the init block, resulting
    // in the top level test cases being available on the spec class
    runBlocking {
      interceptSpec(spec) {
        topLevelTests.forEach { (testCase, active) ->
          // each spec is allocated it's own thread so we can block here safely
          // allowing us to enter the coroutine world
          executor.execute(testCase, active, Context(testCase.description, this.coroutineContext)) { result -> results[testCase] = result }
        }
      }
    }

    return results
  }
}