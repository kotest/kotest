package io.kotlintest

import io.kotlintest.matchers.Matchers
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import org.junit.runners.model.TestTimedOutException
import java.io.Closeable
import java.util.*
import java.util.concurrent.Executors

@RunWith(KTestJUnitRunner::class)
abstract class TestBase : Matchers {

  private val closeablesInReverseOrder = LinkedList<Closeable>()

  // TODO change to true, because one instance per test is a safer default
  open val oneInstancePerTest = false

  // the root test suite which uses the simple name of the class as the name of the suite
  // spec implementations will add their tests to this suite
  internal val root = TestSuite(javaClass.simpleName, ArrayList<TestSuite>(), ArrayList<TestCase>())

  // returns a jUnit Description for the currently registered tests
  internal fun getDescription(): Description = descriptionForSuite(root)

  internal fun run(notifier: RunNotifier) {
    if (oneInstancePerTest) runOneInstancePerTest(notifier)
    else runSharedInstance(notifier)
  }

  protected fun <T: Closeable>autoClose(closeable: T): T {
    closeablesInReverseOrder.addFirst(closeable)
    return closeable
  }

  // this should live in some matchers class, but can't inline in an interface :(
  inline fun <reified T> shouldThrow(thunk: () -> Any): T {
    val e = try {
      thunk()
      null
    } catch (e: Throwable) {
      e
    }

    if (e == null)
      throw TestFailedException("Expected exception ${T::class.qualifiedName} but no exception was thrown")
    else if (e.javaClass.name != T::class.qualifiedName)
      throw TestFailedException("Expected exception ${T::class.qualifiedName} but ${e.javaClass.name} was thrown")
    else
      return e as T
  }


  private fun runOneInstancePerTest(notifier: RunNotifier): Unit {
    val testCount = root.size
    for (k in (0..testCount - 1)) {
      val instance = javaClass.newInstance()
      val testcase = instance.root.tests()[k]
      if (testcase.active() && testcase.isTagged) {
        instance.beforeAll()
        instance.afterEach()
        runTest(testcase, notifier, testcase.description)
        instance.afterEach()
        instance.performAfterAll()
      }
    }
  }

  private fun runSharedInstance(notifier: RunNotifier): Unit {
    beforeAll()
    val tests = root.tests()
    tests.filter { it.isTagged }.filter { it.active() }.forEach { testcase ->
      beforeEach()
      runTest(testcase, notifier, testcase.description)
      afterEach()
    }
    performAfterAll()
  }

  private fun runTest(testcase: TestCase, notifier: RunNotifier, description: Description): Unit {
    val executor =
            if (testcase.config.threads < 2) Executors.newSingleThreadExecutor()
            else Executors.newFixedThreadPool(testcase.config.threads)
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
      val failure = Failure(description, TestTimedOutException(timeout.amount, timeout.timeUnit))
      notifier.fireTestFailure(failure)
    }
  }

  internal fun descriptionForSuite(suite: TestSuite): Description {
    val desc = Description.createSuiteDescription(suite.name.replace('.', ' '))
    for (nestedSuite in suite.nestedSuites) {
      desc.addChild(descriptionForSuite(nestedSuite))
    }
    for (case in suite.cases) {
      desc.addChild(case.description)
    }
    return desc
  }

  open fun beforeAll(): Unit {
  }

  open fun beforeEach(): Unit {
  }

  open fun afterEach(): Unit {
  }

  open fun afterAll(): Unit {
  }

  internal fun performAfterAll() {
    afterAll()
    closeablesInReverseOrder.forEach { it.close() }
  }
}