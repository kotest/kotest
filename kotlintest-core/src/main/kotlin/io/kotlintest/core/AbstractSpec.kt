package io.kotlintest.core

import java.io.Closeable
import java.util.*

/**
 * The parent class of all specs in KotlinTest.
 * A spec is a collection of testcases, where a testcase is an individual 'unit test'.
 *
 * Each implementation of AbstractSpec offers a different way to
 * structure your testcases. For example, the FunSpec is the
 * familiar "method per testcase" that is popular with JUnit.
 *
 * When a testcase is defined in a spec, it is refied as an instance of
 * a [TestCase]. By representing each testcase in this manner,
 * different underlying platforms can be used to actually execute the tests.
 *
 * This means KotlinTest can run your tests using a JVM platform runner, or a
 * JS platform runner.
 */
abstract class AbstractSpec {

  // override this value if you want a new instance of the spec class for each test case
  internal open val oneInstancePerTest = true

  private val closeablesInReverseOrder = LinkedList<Closeable>()

  // the root descriptor for specs
  // specs should add intermediate descriptors to this
  protected val specDescriptor = TestCaseDescriptor("")

  /**
   * Registers a field for auto closing after all tests have run.
   */
  protected fun <T : Closeable> autoClose(closeable: T): T {
    closeablesInReverseOrder.addFirst(closeable)
    return closeable
  }

  /**
   * Intercepts the call of each testcase.
   *
   * Override this function if you wish to control the way each test
   * case is executed.
   *
   * Don't forget to call `test()` in the body of this method.
   * Otherwise the test case will never be executed.
   */
  internal open fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
    test()
  }

  /**
   * Intercepts the call of the spec class.
   *
   * Override this function if you wish to control the way each spec
   * is executed.
   *
   * This means this interceptor will be called once, before any of the
   * testcases in the spec are executed.
   *
   * To continue execution of this spec class, you must invoke the spec
   * function. If you don't want to continue with the execution of the spec,
   * then do not invoke the spec function.
   */
  internal open fun interceptSpec(spec: () -> Unit) {
    spec()
  }

  /**
   * Interceptors that intercepts the execution of the whole spec.
   * Interceptors are executed from left to right.
   */
  internal open val specInterceptors: List<(AbstractSpec, () -> Unit) -> Unit> = listOf()

  /**
   * Config applied to each test case if not overridden per test case.
   */
  protected open val defaultTestCaseConfig: TestCaseConfig = TestCaseConfig()

  internal fun closeResources() {
    closeablesInReverseOrder.forEach { it.close() }
  }
}