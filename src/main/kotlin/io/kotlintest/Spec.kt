package io.kotlintest

import io.kotlintest.matchers.Matcher
import io.kotlintest.matchers.Matchers
import io.kotlintest.matchers.Result
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
abstract class Spec : PropertyTesting(), Matchers, TableTesting {

  protected open val oneInstancePerTest = true

  // the root test suite which uses the simple name of the class as the name of the suite
  // spec implementations will add their tests to this suite
  protected val root = TestSuite(javaClass.simpleName)

  // returns a jUnit Description for the currently registered tests
  val description: Description = root.description

  /**
   * Config applied to each test case if not overridden per test case.
   *
   * Initialize this property by calling the config method.
   * @see config
   * @see Spec.config
   */
  protected open val defaultTestCaseConfig: TestCaseConfig = TestCaseConfig()

  /**
   * Interceptors that intercepts the execution of the whole spec. Interceptors are executed from
   * left to right.
   */
  protected open val specInterceptors: List<(Spec, () -> Unit) -> Unit> = listOf()

  private val closeablesInReverseOrder = LinkedList<Closeable>()

  fun run(notifier: RunNotifier) {
    Project.beforeAll()

    val initialInterceptor = { context: Spec, testCase: () -> Unit ->
      interceptSpec(context, { testCase() })
    }
    val interceptorChain = createInterceptorChain(specInterceptors, initialInterceptor)

    interceptorChain(this, {
      if (oneInstancePerTest)
        runOneInstancePerTest(notifier)
      else
        runSharedInstance(notifier)
    })

    closeResources(notifier)
    Project.afterAll()
  }

  // Creates a new TestConfig (to be assigned to [defaultTestCaseConfig]).
  protected fun config(enabled: Boolean = true,
                       invocations: Int = 1,
                       timeout: Duration = Duration.unlimited,
                       threads: Int = 1,
                       tags: Set<Tag> = setOf(),
                       interceptors: List<(TestCaseContext, () -> Unit) -> Unit> = listOf()): TestCaseConfig =
      TestCaseConfig(enabled, invocations, timeout, threads, tags, interceptors)

  /**
   * Registers a field for auto closing after all tests have run.
   */
  protected fun <T : Closeable> autoClose(closeable: T): T {
    closeablesInReverseOrder.addFirst(closeable)
    return closeable
  }

  // this should live in some matchers class, but can't inline in an interface :(
  inline fun <reified T : Any> beOfType() = object : Matcher<T> {
    val exceptionClassName = T::class.qualifiedName
    override fun test(value: T) = Result(value.javaClass == T::class.java, "$value should be of type $exceptionClassName")
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
    val testCount = root.testCasesIncludingChildren().size
    for (testCaseIndex in (0..testCount - 1)) {
      val instance = javaClass.newInstance()
      val testCase = instance.root[testCaseIndex]
      runTest(instance, testCase, notifier)
    }
  }

  private fun runSharedInstance(notifier: RunNotifier): Unit {
    val testCases = root.testCasesIncludingChildren()
    testCases.forEach { runTest(this, it, notifier) }
  }

  // TODO beautify
  private fun runTest(
      spec: Spec,
      testCase: TestCase,
      notifier: RunNotifier): Unit {
    if (testCase.isActive) {
      val executor =
          if (testCase.config.threads < 2) Executors.newSingleThreadExecutor()
          else Executors.newFixedThreadPool(testCase.config.threads)
      notifier.fireTestStarted(testCase.description)
      val initialInterceptor = { context: TestCaseContext, testCase: () -> Unit ->
        interceptTestCase(context, { testCase() })
      }
      val interceptorChain = createInterceptorChain(testCase.config.interceptors, initialInterceptor)
      val testCaseContext = TestCaseContext(spec, testCase)
      val results = ArrayList<Future<Any>>()
      for (j in 1..testCase.config.invocations) {
        val callable = Callable {
          try {
            interceptorChain(testCaseContext, { testCase.test() })
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
      notifier.fireTestFinished(testCase.description)
    } else {
      notifier.fireTestIgnored(testCase.description)
    }
  }

  private fun <CONTEXT> createInterceptorChain(
      interceptors: Iterable<(CONTEXT, () -> Unit) -> Unit>,
      initialInterceptor: (CONTEXT, () -> Unit) -> Unit): (CONTEXT, () -> Unit) -> Unit {
    return interceptors.reversed().fold(initialInterceptor) { a, b ->
      {
        context: CONTEXT, testCase: () -> Unit -> b(context, { a.invoke(context, { testCase() }) })
      }
    }
  }

  /**
   * Intercepts the call of each test case.
   *
   * Don't forget to call `test()` in the body of this method. Otherwise the test case will never be
   * executed.
   */
  protected open fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
    test()
  }

  /**
   * Intercepts the call of whole spec.
   *
   * Don't forget to call `spec()` in the body of this method. Otherwise the spec will never be
   * executed.
   */
  protected open fun interceptSpec(context: Spec, spec: () -> Unit) {
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