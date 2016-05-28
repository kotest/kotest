package io.kotlintest

import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier

class KTestJUnitRunner(val testClass: Class<TestBase>) : Runner() {

  val instance = testClass.newInstance()
  val root = instance.root

  override fun getDescription(): Description? = instance.getDescription()

  private fun runOneInstancePerTest(notifier: RunNotifier): Unit {
    val testCount = getTests(root).size
    for (k in (0..testCount - 1)) {
      val instance2 = testClass.newInstance()
      val testcase = getTests(instance2.root)[k]
      val desc = instance2.descriptionForTest(testcase)
      try {
        instance2.beforeAll()
        instance2.beforeEach()
        notifier.fireTestStarted(desc)
        testcase.test()
      } catch(e: Throwable) {
        notifier.fireTestFailure(Failure(desc, e))
      } finally {
        notifier.fireTestFinished(desc)
        instance2.afterEach()
        instance2.afterAll()
      }
    }
  }

  private fun runSharedInstance(notifier: RunNotifier): Unit {
    instance.beforeAll()
    val tests = getTests(root)
    tests.forEach { testcase ->
      val desc = instance.descriptionForTest(testcase)
      try {
        notifier.fireTestStarted(desc)
        instance.beforeEach()
        testcase.test()
      } catch(e: Throwable) {
        notifier.fireTestFailure(Failure(desc, e))
      } finally {
        notifier.fireTestFinished(desc)
        instance.afterEach()
      }
    }
    instance.afterAll()
  }

  override fun run(notifier: RunNotifier?): Unit {
    if (instance.oneInstancePerTest) runOneInstancePerTest(notifier!!)
    else runSharedInstance(notifier!!)
  }

  private fun getTests(suite: TestSuite): List<TestCase> {
    return suite.cases + suite.nestedSuites.flatMap { suite -> getTests(suite) }
  }
}