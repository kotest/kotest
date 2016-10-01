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
  val root = TestSuite(javaClass.simpleName, ArrayList<TestSuite>(), ArrayList<TestCase>())

  // returns a jUnit Description for the currently registered tests
  val description: Description
    get() = descriptionForSuite(root)

  /**
   * Config applied to each test case if not overridden per test case.
   *
   * Initialize this property by calling the config method.
   * @see config
   * @see TestBase.config
   */
  protected open val defaultTestCaseConfig: TestConfig = TestConfig()

  private val closeablesInReverseOrder = LinkedList<Closeable>()

  fun run(notifier: RunNotifier) {
    Project.beforeAll()
    if (oneInstancePerTest)
      runOneInstancePerTest(notifier)
    else
      runSharedInstance(notifier)
    closeResources(notifier)
    Project.afterAll()
  }

  // Creates a new TestConfig (to be assigned to [defaultTestCaseConfig]).
  protected fun config(ignored: Boolean = false,
                       invocations: Int = 1,
                       timeout: Duration = Duration.unlimited,
                       threads: Int = 1,
                       tags: Set<Tag> = setOf(),
                       tag: Tag? = null,
                       interceptors: Iterable<TestCaseInterceptor> = listOf()): TestConfig =
      TestConfig(ignored, invocations, timeout, threads, tags, tag, interceptors)

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

      val initial = object : TestCaseInterceptor {
        override fun invoke(context: TestCaseContext, testCase: () -> Unit) {
          aroundTest(context, { testCase() })
        }
      }
      val interceptorChain = testCase.config.interceptors.reversed().fold(initial) {
        a: TestCaseInterceptor, b: TestCaseInterceptor ->
        object : TestCaseInterceptor {
          override fun invoke(context: TestCaseContext, testCase: () -> Unit) {
            b.invoke(context, { a.invoke(context, { testCase() }) })
          }
        }
      }

      val testCaseContext = TestCaseContext(spec, testCase)
      val results = ArrayList<Future<Any>>()
      for (j in 1..testCase.config.invocations) {
        val callable = Callable {
          try {
            interceptorChain.invoke(testCaseContext, { testCase.test() })
          } catch (exception: Throwable) {
            Failure(testCase.description, exception)
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

  protected open fun aroundTest(context: TestCaseContext, test: () -> Unit) {
    test()
  }

  protected open fun aroundSpec(context: TestBase, spec: () -> Unit) {
    spec()
  }

  private fun closeResources(notifier: RunNotifier) {
    closeablesInReverseOrder.forEach {
      try {
        it.close()
      } catch(exception: AssertionError) {
        notifier.fireTestFailure(Failure(description, exception))
      }
    }
  }
}