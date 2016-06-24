package io.kotlintest

import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import org.junit.runners.model.TestTimedOutException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class KTestJUnitRunner(val testClass: Class<TestBase>) : Runner() {

  private val instance = testClass.newInstance()
  private val root: TestSuite = instance.root

  override fun getDescription(): Description? = instance.getDescription()

  override fun run(notifier: RunNotifier?): Unit {
    if (instance.oneInstancePerTest) runOneInstancePerTest(notifier!!)
    else runSharedInstance(notifier!!)
  }

  // TODO try to move logic to execute performAfterAll, afterEach, beforeEach, beforeAll to TestBase
  private fun runOneInstancePerTest(notifier: RunNotifier): Unit {
    val testCount = getTests(root).size // TODO move to TestSuite
    for (k in (0..testCount - 1)) {
      val instance2 = testClass.newInstance()
      val testcase = getTests(instance2.root)[k]
      if (testcase.active() && isTagged(testcase)) {
        val desc = instance.descriptionForTest(testcase)
        instance2.performBeforeAll()
        instance2.performAfterEach()
        runTest(testcase, notifier, desc!!)
        instance2.performAfterEach()
        instance2.performAfterAll()
      }
    }
  }

  private fun runSharedInstance(notifier: RunNotifier): Unit {
    instance.performBeforeAll()
    val tests = getTests(root)
    tests.filter { isTagged(it) }.filter { it.active() }.forEach { testcase ->
      val desc = instance.descriptionForTest(testcase)
      instance.performBeforeEach()
      runTest(testcase, notifier, desc!!)
      instance.performAfterEach()
    }
    instance.performAfterAll()
  }

  // TODO move to TestStuite (and remove `get` prefix)
  private fun getTests(suite: TestSuite): List<TestCase> =
      suite.cases + suite.nestedSuites.flatMap { suite -> getTests(suite) }

  private fun isTagged(testcase: TestCase): Boolean {
    val systemTags = (System.getProperty("testTags") ?: "").split(',')
    return systemTags.isEmpty() || testcase.config.tags.isEmpty() || systemTags.intersect(testcase.config.tags).isNotEmpty()
  }

  private fun runTest(testcase: TestCase, notifier: RunNotifier, description: Description): Unit {
    // TODO inline
    fun executorForTests(): ExecutorService =
        if (testcase.config.threads < 2) Executors.newSingleThreadExecutor()
        else Executors.newFixedThreadPool(testcase.config.threads)

    val executor = executorForTests()
    notifier.fireTestStarted(description)
    for (j in 1..testcase.config.invocations) {
      executor.submit {
        try {
          testcase.test()
        } catch(e: Throwable) {
          notifier.fireTestFailure(Failure(description, e))
        }
      }
    }
    notifier.fireTestFinished(description)
    executor.shutdown()
    val timeout = testcase.config.timeout
    val terminated = executor.awaitTermination(timeout.amount, timeout.timeUnit)
    if (!terminated) {
      notifier.fireTestFailure(Failure(description, TestTimedOutException(timeout.amount, timeout.timeUnit)))
    }
  }
}