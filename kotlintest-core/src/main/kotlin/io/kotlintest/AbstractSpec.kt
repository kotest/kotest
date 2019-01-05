package io.kotlintest

import org.junit.platform.commons.annotation.Testable
import java.io.Closeable
import java.util.*

@Testable
abstract class AbstractSpec : Spec {

  override fun isInstancePerTest(): Boolean = false

  private val rootTestCases = mutableListOf<TestCase>()

  override fun testCases(): List<TestCase> = rootTestCases.toList()

  protected fun createTestCase(name: String, test: suspend TestContext.() -> Unit, config: TestCaseConfig, type: TestType) =
      TestCase(description().append(name), this, test, lineNumber(), type, config)

  protected fun addTestCase(name: String, test: suspend TestContext.() -> Unit, config: TestCaseConfig, type: TestType) {
    if (rootTestCases.any { it.name == name })
      throw IllegalArgumentException("Cannot add test with duplicate name $name")
    rootTestCases.add(createTestCase(name, test, config, type))
  }

  private val closeablesInReverseOrder = LinkedList<Closeable>()

  /**
   * Registers a field for auto closing after all tests have run.
   */
  protected fun <T : Closeable> autoClose(closeable: T): T {
    closeablesInReverseOrder.addFirst(closeable)
    return closeable
  }

  override fun closeResources() {
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