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
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

@RunWith(KTestJUnitRunner::class)
abstract class TestBase : PropertyTesting(), Matchers, TableTesting {

  protected open val oneInstancePerTest = true

  // the root test suite which uses the simple name of the class as the name of the suite
  // spec implementations will add their tests to this suite
  internal val root = TestSuite(javaClass.simpleName, ArrayList<TestSuite>(), ArrayList<TestCase>())

  // returns a jUnit Description for the currently registered tests
  internal val description: Description
    get() = descriptionForSuite(root)

  /**
   * Config applied to each test case if not overridden per test case.
   *
   * Initialize this property by calling the config method.
   * @see config
   * @see TestBase.config
   */
  protected open val defaultTestCaseConfig: TestConfig = TestConfig()

  /**
   * Extensions with methods to be executed before or after the tests. Extensions will be processed
   * from left to right.
   */
  protected open val extensions: List<SpecExtension> = listOf()

  private val closeablesInReverseOrder = LinkedList<Closeable>()

  internal fun run(notifier: RunNotifier) {
    performBeforeAll()

    if (oneInstancePerTest)
      runOneInstancePerTest(notifier)
    else
      runSharedInstance(notifier)

    performAfterAll(notifier)

  }

  // Creates a new TestConfig (to be assigned to [defaultTestCaseConfig]).
  protected fun config(ignored: Boolean = false,
                       invocations: Int = 1,
                       timeout: Duration = Duration.unlimited,
                       threads: Int = 1,
                       tags: Set<Tag> = setOf(),
                       tag: Tag? = null): TestConfig =
      TestConfig(ignored, invocations, timeout, threads, tags, tag)

  /**
   * Registers a field for auto closing after all tests have run.
   */
  protected fun <T : Closeable> autoClose(closeable: T): T {
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
    for (testCaseIndex in (0..testCount - 1)) {
      val instance = javaClass.newInstance()
      val testcase = instance.root.tests()[testCaseIndex]
      runTest(instance, testcase, notifier)
    }
  }

  private fun runSharedInstance(notifier: RunNotifier): Unit {
    val tests = root.tests()
    tests.forEach { runTest(this, it, notifier) }
  }

  // TODO beautify
  private fun runTest(
      spec: TestBase,
      testCase: TestCase,
      notifier: RunNotifier): Unit {
    if (testCase.isActive) {
      val executor =
          if (testCase.config.threads < 2) Executors.newSingleThreadExecutor()
          else Executors.newFixedThreadPool(testCase.config.threads)
      notifier.fireTestStarted(testCase.description)
      val results = ArrayList<Future<Any>>()
      for (j in 1..testCase.config.invocations) {
        val callable = Callable {
          performBeforeEach(spec, testCase)
          try {
            try {
              testCase.test()
              performAfterEach(spec, testCase, null)
            } catch (exception: Throwable) {
              performAfterEach(spec, testCase, exception)
              // afterEach would need to rethrow the exception, to gain a failure
              // alternative: throw exception
            }
          } catch (e: Throwable) {
            Failure(testCase.description, e)
          }
        }
        results.add(executor.submit(callable))
      }
      executor.shutdown()
      val timeout = testCase.config.timeout
      val terminated = executor.awaitTermination(timeout.amount, timeout.timeUnit)
      results.forEach {
        val result = it.get()
        if (result is Failure) {
          notifier.fireTestFailure(result)
        }
      }
      if (!terminated) {
        val failure = Failure(description, TestTimedOutException(timeout.amount, timeout.timeUnit))
        notifier.fireTestFailure(failure)
      }
      notifier.fireTestFinished(description)
    } else {
      notifier.fireTestIgnored(testCase.description)
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

  protected open fun beforeAll(): Unit {
  }

  protected open fun beforeEach(): Unit {
  }

  protected open fun afterEach(): Unit {
  }

  protected open fun afterAll(): Unit {
  }

  private fun performBeforeAll(): Unit {
    Project.beforeAll()
    extensions.forEach { extension -> extension.beforeAll(this) }
    beforeAll()
  }

  private fun performBeforeEach(spec: TestBase, testCase: TestCase): Unit {
    spec.extensions.forEach { extension ->
      val context = TestCaseContext(spec = this, testCase = testCase)
      extension.beforeEach(context)
    }
    spec.beforeEach()
    beforeEach()
  }

  private fun performAfterEach(spec: TestBase, testCase: TestCase, failure: Throwable?): Unit {
    spec.afterEach()
    spec.extensions.forEach { extension ->
      val context = TestCaseContext(spec = this, testCase = testCase, failure = failure)
      extension.afterEach(context)
    }
  }

  private fun performAfterAll(notifier: RunNotifier) {
    afterAll()
    extensions.forEach { extension -> extension.afterAll(this) }
    Project.afterAll()
    closeablesInReverseOrder.forEach {
      try {
        it.close()
      } catch(exception: AssertionError) {
        notifier.fireTestFailure(Failure(description, exception))
      }
    }
  }
}