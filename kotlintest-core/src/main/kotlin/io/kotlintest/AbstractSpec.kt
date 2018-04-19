package io.kotlintest

import java.io.Closeable
import java.util.*

abstract class AbstractSpec : Spec {

  override fun isInstancePerTest(): Boolean = false

  private val rootScopes = mutableListOf<Scope>()

  protected fun addRootScope(scope: Scope) {
    if (rootScopes.any { it.name() == scope.name() })
      throw IllegalArgumentException("Cannot add scope with duplicate name ${scope.name()}")
    rootScopes.add(scope)
  }

  internal fun rootDescription() = Description(emptyList(), name())

  override fun root(): TestContainer = TestContainer(rootDescription(), this::class, { context -> rootScopes.forEach { context.executeScope(it) } })

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
   * Config applied to each test case if not overridden per test case.
   */
  protected open val defaultTestCaseConfig: TestCaseConfig = TestCaseConfig()
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DisplayName(val name: String)