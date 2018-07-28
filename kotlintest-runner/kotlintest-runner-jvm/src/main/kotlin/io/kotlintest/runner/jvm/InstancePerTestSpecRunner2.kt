package io.kotlintest.runner.jvm

import arrow.core.Failure
import arrow.core.Success
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestContext
import org.slf4j.LoggerFactory
import java.util.*

class InstancePerTestSpecRunner2(listener: TestEngineListener) : SpecRunner(listener) {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  private val executed = HashSet<Description>()
  private val discovered = HashSet<Description>()
  private val queue = ArrayDeque<TestCase>()

  /**
   * When executing a [TestCase], any child test cases that are found, are placed onto
   * a stack. When the test case has completed, we take the next test case from the
   * stack, and begin executing that.
   */
  override fun execute(spec: Spec) {
    topLevelTests(spec).forEach { enqueue(it) }
    logger.debug("queue=$queue")
    while (queue.isNotEmpty()) {
      val element = queue.removeFirst()
      logger.debug("Retrieving element from queue: ${element.name}")
      execute(element)
    }
    logger.debug("Final queue=$queue")
  }

  private fun enqueue(testCase: TestCase) {
    if (!discovered.contains(testCase.description)) {
      discovered.add(testCase.description)
      queue.add(testCase)
    }
    logger.debug("new queue=$queue")
  }

  // todo add checks for duplicate test names at runtime
  //if (executed.contains(testCase.description))
  //throw IllegalStateException("Cannot add duplicate test name ${testCase.name}")

  /**
   * The intention of this runner is that each [TestCase] executes in it's own instance
   * of the containing [Spec] class. Therefore, when we begin executing a test case from
   * the queue, we must first instantiate a new spec, and begin execution on _that_ instance.
   *
   * As test lambdas are executed, nested test cases will be registered, these should be ignored
   * if they are not an ancestor of the target. If they are then we can step into them, and
   * continue recursively until we find the target.
   *
   * One the target is found it can be executed as normal, and any test lambdas it contains
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
        override fun registerTestCase(testCase: TestCase) {
          enqueue(testCase)
        }
      }
      io.kotlintest.runner.jvm.TestCaseExecutor(listener, current, context).execute()
      // otherwise if it's an ancestor then we want to search it recursively
    } else if (current.description.isAncestorOf(target)) {
      current.test.invoke(object : TestContext() {
        override fun description(): Description = current.description
        override fun registerTestCase(testCase: TestCase) = locate(target, testCase)
      })
    }
  }
}