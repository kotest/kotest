package io.kotlintest.runner.jvm.spec

import arrow.core.Failure
import arrow.core.Success
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestContext
import io.kotlintest.TestResult
import io.kotlintest.runner.jvm.TestCaseExecutor
import io.kotlintest.runner.jvm.TestEngineListener
import io.kotlintest.runner.jvm.instantiateSpec
import org.slf4j.LoggerFactory
import java.util.*

class InstancePerNodeSpecRunner(listener: TestEngineListener) : SpecRunner(listener) {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  private val executed = HashSet<Description>()
  private val results = HashMap<TestCase, TestResult>()
  private val discovered = HashSet<Description>()
  private val queue = ArrayDeque<TestCase>()

  /**
   * When executing a [TestCase], any child test cases that are found, are placed onto
   * a stack. When the test case has completed, we take the next test case from the
   * stack, and begin executing that.
   */
  override fun execute(spec: Spec, active: List<TestCase>, inactive: List<TestCase>): Map<TestCase, TestResult> {
    active.forEach { enqueue(it) }
    while (queue.isNotEmpty()) {
      val element = queue.removeFirst()
      execute(element)
    }
    return results
  }

  private fun enqueue(testCase: TestCase) {
    if (discovered.contains(testCase.description))
      throw IllegalStateException("Cannot add duplicate test name ${testCase.name}")
    discovered.add(testCase.description)
    logger.debug("Enqueuing test ${testCase.description.fullName()}")
    queue.add(testCase)
  }

  /**
   * The intention of this runner is that each [TestCase] executes in it's own instance
   * of the containing [Spec] class. Therefore, when we begin executing a test case from
   * the queue, we must first instantiate a new spec, and begin execution on _that_ instance.
   *
   * As test lambdas are executed, nested test cases will be registered, these should be ignored
   * if they are not an ancestor of the target. If they are then we can step into them, and
   * continue recursively until we find the target.
   *
   * Once the target is found it can be executed as normal, and any test lambdas it contains
   * can be registered back with the stack for execution later.
   */
  private fun execute(testCase: TestCase) {
    logger.debug("Executing $testCase")
    instantiateSpec(testCase.spec::class).let {
      when (it) {
        is Failure -> throw it.exception
        is Success -> {
          val spec = it.value
          interceptSpec(spec) {
            spec.testCases().forEach { locate(testCase.description, it) }
          }
        }
      }
    }
  }

  private fun locate(target: Description, current: TestCase) {
    // if equals then we've found the test we want to invoke
    if (target == current.description) {
      val context = object : TestContext() {
        override fun description(): Description = target
        override fun registerTestCase(testCase: TestCase) = enqueue(testCase)
      }
      if (executed.contains(target))
        throw  IllegalStateException("Attempting to execute duplicate test")
      executed.add(target)
      TestCaseExecutor(listener, current, context).execute()
      // otherwise if it's an ancestor then we want to search it recursively
    } else if (current.description.isAncestorOf(target)) {
      current.test.invoke(object : TestContext() {
        override fun description(): Description = current.description
        override fun registerTestCase(testCase: TestCase) = locate(target, testCase)
      })
    }
  }
}