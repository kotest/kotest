package io.kotlintest

import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import org.junit.runners.model.TestTimedOutException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class KTestJUnitRunner(val testClass: Class<TestBase>) : Runner() {

  val instance = testClass.newInstance()
  val root = instance.root

  override fun getDescription(): Description? = instance.getDescription()

  private fun runOneInstancePerTest(notifier: RunNotifier): Unit {
    val testCount = getTests(root).size
    for (k in (0..testCount - 1)) {
      val instance2 = testClass.newInstance()
      val testcase = getTests(instance2.root)[k]
      if (testcase.active() && isTagged(testcase)) {
        val desc = instance.descriptionForTest(testcase)
        instance2.beforeAll()
        instance2.beforeEach()
        runTest(testcase, notifier, desc!!)
        instance2.afterEach()
        instance2.afterAll()
      }
    }
  }

  private fun runSharedInstance(notifier: RunNotifier): Unit {
    instance.beforeAll()
    val tests = getTests(root)
    tests.filter { isTagged(it) }.filter { it.active() }.forEach { testcase ->
      val desc = instance.descriptionForTest(testcase)
      instance.beforeEach()
      runTest(testcase, notifier, desc!!)
      instance.afterEach()
    }
    instance.afterAll()
  }

  private fun getTests(suite: TestSuite): List<TestCase> =
      suite.cases + suite.nestedSuites.flatMap { suite -> getTests(suite) }

  private fun isTagged(testcase: TestCase): Boolean {
    val systemTags = (System.getProperty("testTags") ?: "").split(',')
    return systemTags.isEmpty() || testcase.config.tags.isEmpty() || systemTags.intersect(testcase.config.tags).isNotEmpty()
  }

  private fun runTest(testcase: TestCase, notifier: RunNotifier, desc: Description): Unit {

    fun executorForTests(): ExecutorService =
        if (testcase.config.threads < 2) Executors.newSingleThreadExecutor()
        else Executors.newFixedThreadPool(testcase.config.threads)

    val executor = executorForTests()
    notifier.fireTestStarted(desc)
    for (j in 1..testcase.config.invocations) {
      executor.submit {
        try {
          testcase.test()
        } catch(e: Throwable) {
          notifier.fireTestFailure(Failure(desc, e))
        }
      }
    }
    notifier.fireTestFinished(desc)
    executor.shutdown()
    if (testcase.config.timeout > 0) {
      if (!executor.awaitTermination(testcase.config.timeout, testcase.config.timeoutUnit)) {
        notifier.fireTestFailure(Failure(desc, TestTimedOutException(testcase.config.timeout, testcase.config.timeoutUnit)))
      }
    } else {
      executor.awaitTermination(1, TimeUnit.DAYS)
    }
  }

  override fun run(notifier: RunNotifier?): Unit {
    if (instance.oneInstancePerTest) runOneInstancePerTest(notifier!!)
    else runSharedInstance(notifier!!)
  }
}