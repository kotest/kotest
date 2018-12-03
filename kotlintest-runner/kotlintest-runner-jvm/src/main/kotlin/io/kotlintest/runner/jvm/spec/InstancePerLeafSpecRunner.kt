package io.kotlintest.runner.jvm.spec

import arrow.core.Failure
import arrow.core.Success
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestContext
import io.kotlintest.TestResult
import io.kotlintest.runner.jvm.TestEngineListener
import io.kotlintest.TestType
import io.kotlintest.runner.jvm.TestCaseExecutor
import io.kotlintest.runner.jvm.instantiateSpec
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Implementation of [SpecRunner] that executes each leaf test (that is a test case
 * of type [TestType.Test]) in a separate instance of the [Spec] class (that is, isolated
 * from other leaf executions).
 *
 * A failure in a parent test will prevent nested tests from running.
 *
 * Each branch test case (that is a test case of type [TestType.Container]) is only
 * executed as part of the execution "path" to the leaf node.
 */
class InstancePerLeafSpecRunner(listener: TestEngineListener) : SpecRunner(listener) {

  private val logger = LoggerFactory.getLogger(this.javaClass)
  private val queue = ArrayDeque<TestCase>()
  private val results = HashMap<TestCase, TestResult>()

  override fun execute(spec: Spec, active: List<TestCase>, inactive: List<TestCase>): Map<TestCase, TestResult> {
    active.forEach { enqueue(it) }
    while (queue.isNotEmpty()) {
      val element = queue.removeFirst()
      execute(element)
    }
    return results
  }

  private fun enqueue(testCase: TestCase) {
    logger.debug("Enqueuing test ${testCase.description.fullName()}")
    queue.add(testCase)
  }

  // starts executing a test case, but we don't know if this test case will be a leaf or a branch.
  // If it turns out to be a leaf, then we're done, lovely.
  // if it's a branch then we should execute the first child it finds immediately, but then subsequent
  // nested tests must be queued
  private fun execute(testCase: TestCase) {
    logger.debug("Executing $testCase")
    // we need to execute on a separate instance of the spec class
    // so we must instantiate a new space, locate the test we're trying to run, and then run it
    instantiateSpec(testCase.spec::class).let { specOrFailure ->
      when (specOrFailure) {
        is Failure -> throw specOrFailure.exception
        is Success -> {
          val spec = specOrFailure.value
          interceptSpec(spec) {
            spec.testCases().forEach { topLevel ->
              locate(topLevel, testCase.description) {
                TestCaseExecutor(listener, it, context(it)).execute()
              }
            }
          }
        }
      }
    }
  }

  private fun context(current: TestCase): TestContext = object : TestContext() {
    private var found = false
    override fun description(): Description = current.description
    override fun registerTestCase(testCase: TestCase) {
      if (found) enqueue(testCase) else {
        found = true
        TestCaseExecutor(listener, testCase, context(testCase)).execute()
      }
    }
  }

  // takes a current test case and a target test case and attempts to locate the target
  // by executing the current and it's nested tests recursively until we find the target
  private fun locate(current: TestCase, target: Description, callback: (TestCase) -> Unit) {
    // If the current test is the same as the target, we've found what we want, and can invoke
    // the callback. Otherwise we must execute the closure and check any registered tests to
    // see if they are on the desired path. If they are, we recurse into it.
    if (current.description == target) callback(current) else if (current.description.isAncestorOf(target)) {
      current.test.invoke(object : TestContext() {
        override fun description(): Description = current.description
        override fun registerTestCase(testCase: TestCase) = locate(testCase, target, callback)
      })
    }
  }
}