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
abstract class AbstractSpec : Spec {

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