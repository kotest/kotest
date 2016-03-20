package io.kotlintest

import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.flatMap
import kotlin.collections.forEach
import kotlin.collections.getOrPut
import kotlin.collections.plus

class KTestJUnitRunner(testClass: Class<TestBase>) : Runner() {

  val map: MutableMap<TestCase, Description> = HashMap()
  val counter = AtomicInteger()
  val root = testClass.newInstance().root

  override fun getDescription(): Description? {
    return descriptionForSuite(root)
  }

  private fun descriptionForSuite(suite: TestSuite): Description? {
    val desc = Description.createSuiteDescription(suite.name, counter.andIncrement)
    suite.suites.forEach { suite ->
      desc.addChild(descriptionForSuite(suite))
    }
    suite.cases.forEach { testcase ->
      desc.addChild(descriptionForTest(suite, testcase))
    }
    return desc
  }

  private fun descriptionForTest(suite: TestSuite, testcase: TestCase): Description? {
    return map.getOrPut(testcase, { Description.createTestDescription(suite.name, testcase.name, counter.andIncrement) })
  }

  override fun run(notifier: RunNotifier?) {
    getTests(root).forEach { testcase ->
      val desc = map[testcase]
      notifier!!.fireTestStarted(desc)
      try {
        testcase.test()
        notifier.fireTestFinished(desc)
      } catch(e: TestFailedException) {
        notifier.fireTestFailure(Failure(desc, e))
      }
    }
  }

  private fun getTests(suite: TestSuite): List<TestCase> {
    return suite.cases + suite.suites.flatMap { suite -> getTests(suite) }
  }
}