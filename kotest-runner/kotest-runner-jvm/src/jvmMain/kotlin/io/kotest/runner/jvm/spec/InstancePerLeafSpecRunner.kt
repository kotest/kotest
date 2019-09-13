package io.kotest.runner.jvm.spec

import arrow.core.Failure
import arrow.core.Success
import io.kotest.Description
import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.TestType
import io.kotest.core.TestContext
import io.kotest.extensions.TopLevelTests
import io.kotest.runner.jvm.TestCaseExecutor
import io.kotest.runner.jvm.TestEngineListener
import io.kotest.runner.jvm.instantiateSpec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.Comparator
import java.util.PriorityQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implementation of [SpecRunner] that executes each leaf test (that is a [TestCase] which
 * has type [TestType.Test]) in a fresh instance of the [Spec] class (that is, isolated
 * from other leaf executions).
 *
 * Each branch test (that is a [TestCase] of type [TestType.Container]) is only
 * executed as part of the execution "path" to the leaf test. In other words, the branch
 * tests are not executed "stand alone".
 *
 * So, given the following structure:
 *
 *  outerTest {
 *    innerTestA {
 *      // test
 *    }
 *    innerTestB {
 *      // test
 *    }
 *  }
 *
 * Two spec instances will be created. The execution process will be:
 *
 * spec1 = instantiate spec
 * spec1.outerTest
 * spec1.innerTestA
 * spec2 = instantiate spec
 * spec2.outerTest
 * spec2.innerTestB
 *
 * A failure in a branch test will prevent nested tests from executing.
 */
class InstancePerLeafSpecRunner(listener: TestEngineListener,
                                listenerExecutor: ExecutorService,
                                scheduler: ScheduledExecutorService) : SpecRunner(listener) {

  private val logger = LoggerFactory.getLogger(this.javaClass)
  private val counter = AtomicInteger(0)

  data class Enqueued(val testCase: TestCase, val count: Int)

  // the queue contains tests discovered to run next. We always run the tests with the "furthest" path first.
  private val queue = PriorityQueue<Enqueued>(Comparator<Enqueued> { o1, o2 ->
    val o1s = o1.testCase.description.names().size
    val o2s = o2.testCase.description.names().size
    if (o1s == o2s) o1.count.compareTo(o2.count) else o2s.compareTo(o1s)
  })

  private val executor = TestCaseExecutor(listener, listenerExecutor, scheduler)
  private val results = mutableMapOf<TestCase, TestResult>()

  override fun execute(spec: Spec, topLevelTests: TopLevelTests): Map<TestCase, TestResult> {
    topLevelTests.tests.forEach { test -> enqueue(test.testCase) }
    while (queue.isNotEmpty()) {
      val element = queue.remove()
      execute(element.testCase)
    }
    return results
  }

  private fun enqueue(testCase: TestCase) {
    logger.trace("Enqueuing test ${testCase.description.fullName()}")
    queue.add(Enqueued(testCase, counter.getAndIncrement()))
  }

  // starts executing an enqueued test case
  private fun execute(testCase: TestCase) {
    logger.trace("Executing $testCase")
    // we need to execute on a separate instance of the spec class
    // so we must instantiate a new spec, locate the test we're trying to run, and then run it
    instantiateSpec(testCase.spec::class).let { specOrFailure ->
      when (specOrFailure) {
        is Failure -> throw specOrFailure.exception
        is Success -> {
          val spec = specOrFailure.value
          // each spec is allocated it's own thread so we can block here safely
          // allowing us to enter the coroutine world
          runBlocking {
            interceptSpec(spec) {
              spec.testCases().forEach { topLevel ->
                locate(topLevel, testCase.description, this)
              }
            }
          }
        }
      }
    }
  }

  /**
   * Takes a target test case description, and a given test case, and attempts
   * to locate the target. There are three possibilities.
   *
   * The given test is the target. In this situation we can execute the given test with
   * a context that will register all nested children for later execution (since this will
   * be the first time each of them has been discovered).
   *
   * The given test is not the target, but is an ancestor of the target. In this situation,
   * we can execute the given test with a context that will perform this lookup logic for
   * it's nested classes (since we don't want to execute other nested children as they will
   * have been seen before).
   *
   * The given test case is neither the target nor an ancestor and then we ignore.
   */
  private suspend fun locate(given: TestCase, target: Description, scope: CoroutineScope) {
    when {
      given.description == target -> executeTarget(given, scope)
      given.description.isAncestorOf(target) -> executeAncestor(given, target, scope)
    }
  }

  private suspend fun executeTarget(testCase: TestCase, scope: CoroutineScope) {
    executor.execute(testCase, context(testCase, scope)) { result -> results[testCase] = result }
  }

  private suspend fun executeAncestor(testCase: TestCase, target: Description, scope: CoroutineScope) {
    // todo I think this should probably go via the executor but with config.threads always = 1
    testCase.test.invoke(locatingContext(testCase, target, scope))
  }

  /**
   * Creates a [TestContext] which will enqueue nested tests, except the first.
   * The first is executed on the same spec instance because we only want a fresh
   * spec when we next execute a leaf.
   */
  private fun context(current: TestCase, scope: CoroutineScope): TestContext = object : TestContext(scope.coroutineContext) {
    private var first = false
    override fun description(): Description = current.description
    override suspend fun registerTestCase(testCase: TestCase) {
      if (first) enqueue(testCase) else {
        first = true
        executor.execute(testCase, context(testCase, scope)) { result -> results[testCase] = result }
      }
    }
  }

  /**
   * Creates a [TestContext] for a given [TestCase] which will delegate registered
   * tests back to the locate method.
   *
   * Executing a registered test should suspend the current test until it completes.
   */
  private fun locatingContext(given: TestCase, target: Description, scope: CoroutineScope) = object : TestContext(scope.coroutineContext) {
    override fun description(): Description = given.description
    override suspend fun registerTestCase(testCase: TestCase) = locate(testCase, target, scope)
  }
}
