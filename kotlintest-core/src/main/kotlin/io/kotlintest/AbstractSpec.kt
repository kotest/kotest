package io.kotlintest

import java.io.Closeable
import java.util.*

abstract class AbstractSpec : Spec {

  // override this value if you want a new instance of the spec class for each test case
  open val oneInstancePerTest = true

  // the root container for specs
  // specs should add intermediate containers to this
  internal val rootScope: TestScope by lazy {
    TestScope(name(), this@AbstractSpec, {})
  }

  override fun root(): TestScope = rootScope

  fun name(): String {
    val displayName = this::class.annotations.find { it is DisplayName }
    return when (displayName) {
      is DisplayName -> displayName.name
      else -> javaClass.simpleName
    }
  }

  override fun isInstancePerTest(): Boolean = oneInstancePerTest

  private val closeablesInReverseOrder = LinkedList<Closeable>()

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
  open val specInterceptors: List<(Spec, () -> Unit) -> Unit> = listOf()

  /**
   * Config applied to each test case if not overridden per test case.
   */
  protected open val defaultTestCaseConfig: TestCaseConfig = TestCaseConfig()

  internal fun closeResources() {
    closeablesInReverseOrder.forEach { it.close() }
  }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DisplayName(val name: String)