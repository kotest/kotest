package io.kotlintest

import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory
import java.io.Closeable
import java.util.*

abstract class AbstractSpec : Spec {

  @TestFactory
  fun primer() = emptyList<DynamicNode>()

  override fun isInstancePerTest(): Boolean = false

  private val rootTestCases = mutableListOf<TestScope>()

  override fun testCases(): List<TestScope> = rootTestCases.toList()

  protected fun createTestCase(name: String, test: TestContext.() -> Unit, config: TestCaseConfig) =
      TestScope(description().append(name), this, test, lineNumber(), config)

  protected fun addTestCase(name: String, test: TestContext.() -> Unit, config: TestCaseConfig) {
    if (rootTestCases.any { it.name == name })
      throw IllegalArgumentException("Cannot add test with duplicate name $name")
    rootTestCases.add(createTestCase(name, test, config))
  }

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