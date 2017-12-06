package io.kotlintest

import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.ClassSource
import java.io.Closeable
import java.util.*

abstract class Spec {

  protected val source = ClassSource.from(this.javaClass)

  // the root descriptor for this spec
  internal val specDescriptor =
          SpecDescriptor(
                  UniqueId.root("spec", javaClass.simpleName),
                  javaClass.simpleName,
                  this)

  // override this value if you want a new instance of the spec class for each test case
  internal open val oneInstancePerTest = false

  /**
   * Config applied to each test case if not overridden per test case.
   */
  protected open val defaultTestCaseConfig: TestCaseConfig = TestCaseConfig()

  /**
   * Interceptors that intercepts the execution of the whole spec.
   * Interceptors are executed from left to right.
   */
  internal open val specInterceptors: List<(Spec, () -> Unit) -> Unit> = listOf()

  private val closeablesInReverseOrder = LinkedList<Closeable>()

  /**
   * Registers a field for auto closing after all tests have run.
   */
  protected fun <T : Closeable> autoClose(closeable: T): T {
    closeablesInReverseOrder.addFirst(closeable)
    return closeable
  }

  /**
   * Intercepts the call of each test case.
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
   * This means this interceptor will be called once, before any of the tests in the spec
   * are executed.
   *
   * To continue execution of this spec class, you must invoke the spec function. If you don't
   * want to continue with the execution of the spec, then do not invoke the spec function.
   */
  internal open fun interceptSpec(spec: () -> Unit) {
    spec()
  }

  internal fun closeResources() {
    closeablesInReverseOrder.forEach { it.close() }
  }
}