package io.kotlintest

import io.kotlintest.matchers.Matchers
import io.kotlintest.properties.PropertyTesting
import io.kotlintest.properties.TableTesting
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import org.junit.runners.model.TestTimedOutException
import java.io.Closeable
import java.util.*
import java.util.concurrent.Executors

@RunWith(KTestJUnitRunner::class)
abstract class TestBase : PropertyTesting(), Matchers, TableTesting {

  private val closeablesInReverseOrder = LinkedList<Closeable>()

  open val oneInstancePerTest = false

  /**
   * Config applied to each test case if not overridden per test case.
   *
   * Initialize this property by calling the config method.
   * @see config
   * @see TestBase.config
   */
  open val defaultTestCaseConfig: TestConfig = config()

  // the root test suite which uses the simple name of the class as the name of the suite
  // spec implementations will add their tests to this suite
  internal val root = TestSuite(javaClass.simpleName, ArrayList<TestSuite>(), ArrayList<TestCase>())

  // returns a jUnit Description for the currently registered tests
  internal val description: Description
    get() = descriptionForSuite(root)

  internal fun run(notifier: RunNotifier) {
    if (oneInstancePerTest) runOneInstancePerTest(notifier)
    else runSharedInstance(notifier)
  }

  protected fun config(ignored: Boolean = false,
                       invocations: Int = 1,
                       timeout: Duration = Duration.unlimited,
                       threads: Int = 1,
                       tags: List<String> = listOf()) =
          TestConfig(ignored, invocations, timeout, threads, tags)

  /**
   * Registers a field for auto closing after all tests have run.
   */
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

    val exceptionClassName = T::class.qualifiedName

    if (e == null)
      throw AssertionError("Expected exception ${T::class.qualifiedName} but no exception was thrown")
    else if (e.javaClass.canonicalName != exceptionClassName)
      throw AssertionError("Expected exception ${T::class.qualifiedName} but ${e.javaClass.name} was thrown", e)
    else
      return e as T
  }

  private fun runOneInstancePerTest(notifier: RunNotifier): Unit {
    val testCount = root.tests().size
    for (k in (0..testCount - 1)) {
      val instance = javaClass.newInstance()
      val testcase = instance.root.tests()[k]
      if (testcase.isActive) {
        instance.beforeAll()
        instance.afterEach()
        runTest(testcase, notifier, testcase.description)
        instance.afterEach()
        instance.performAfterAll(notifier)
      } else {
        notifier.fireTestIgnored(testcase.description)
      }
    }
  }

  private fun runSharedInstance(notifier: RunNotifier): Unit {
    beforeAll()
    val tests = root.tests()
    tests.forEach {
      when {
        it.isActive -> {
          beforeEach()
          runTest(it, notifier, it.description)
          afterEach()
        }
        else -> {
          notifier.fireTestIgnored(it.description)
        }
      }
    }
    performAfterAll(notifier)
  }

  private fun runTest(testcase: TestCase, notifier: RunNotifier, description: Description): Unit {
    val executor =
        if (testcase.config.threads < 2) Executors.newSingleThreadExecutor()
        else Executors.newFixedThreadPool(testcase.config.threads)
    notifier.fireTestStarted(description)
    var failed = false
    for (j in 1..testcase.config.invocations) {
      executor.submit {
        try {
          testcase.test()
        } catch(e: AssertionError) {
          notifier.fireTestFailure(Failure(description, e))
          failed = true
        }
      }
    }
    executor.shutdown()
    val timeout = testcase.config.timeout
    val terminated = executor.awaitTermination(timeout.amount, timeout.timeUnit)
    if (!terminated) {
      val failure = Failure(description, TestTimedOutException(timeout.amount, timeout.timeUnit))
      notifier.fireTestFailure(failure)
      failed = true
    }
    if (!failed)
      notifier.fireTestFinished(description)
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

  protected open fun beforeAll(): Unit {
  }

  protected open fun beforeEach(): Unit {
  }

  protected open fun afterEach(): Unit {
  }

  protected open fun afterAll(): Unit {
  }

  private fun performAfterAll(notifier: RunNotifier) {
    afterAll()
    closeablesInReverseOrder.forEach {
      try {
        it.close()
      } catch(exception: AssertionError) {
        notifier.fireTestFailure(Failure(description, exception))
      }
    }
  }
}