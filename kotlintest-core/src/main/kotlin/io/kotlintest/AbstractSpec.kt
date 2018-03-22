package io.kotlintest

import java.io.Closeable
import java.util.*

abstract class AbstractSpec : Spec {

  override fun isInstancePerTest(): Boolean = false

  internal val rootScope = TestScope()

  override fun root(): TestContainer = TestContainer(name(), this, { rootScope.children.toList() })

  private val closeablesInReverseOrder = LinkedList<Closeable>()

  /**
   * Registers a field for auto closing after all tests have run.
   */
  protected fun <T : Closeable> autoClose(closeable: T): T {
    closeablesInReverseOrder.addFirst(closeable)
    return closeable
  }

  internal fun closeResources() {
    closeablesInReverseOrder.forEach { it.close() }
  }

  /**
   * Interceptors that intercepts the execution of the whole spec.
   * Interceptors are executed from left to right.
   */
  open val specInterceptors: List<(Spec, () -> Unit) -> Unit> = listOf()

  /**
   * Config applied to each test case if not overridden per test case.
   */
  protected open val defaultTestCaseConfig: TestCaseConfig = TestCaseConfig()
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DisplayName(val name: String)