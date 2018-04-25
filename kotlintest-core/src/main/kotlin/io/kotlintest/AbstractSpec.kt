package io.kotlintest

import io.kotlintest.extensions.TestCaseExtension
import java.io.Closeable
import java.time.Duration
import java.util.*

abstract class AbstractSpec : Spec {

  override fun isInstancePerTest(): Boolean = false

  private val rootScopes = mutableListOf<TestScope>()

  protected fun addRootScope(scope: TestScope) {
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

  inner class StringContextBuilder(val context: TestContext) {
    fun String.config(
        invocations: Int? = null,
        enabled: Boolean? = null,
        timeout: Duration? = null,
        threads: Int? = null,
        tags: Set<Tag>? = null,
        extensions: List<TestCaseExtension>? = null,
        test: TestContext.() -> Unit): TestCase {
      val config = TestCaseConfig(
          enabled ?: defaultTestCaseConfig.enabled,
          invocations ?: defaultTestCaseConfig.invocations,
          timeout ?: defaultTestCaseConfig.timeout,
          threads ?: defaultTestCaseConfig.threads,
          tags ?: defaultTestCaseConfig.tags,
          extensions ?: defaultTestCaseConfig.extensions)
      val tc = TestCase(context.currentScope().description().append("should " + this), this@AbstractSpec, test, lineNumber(), config)
      context.executeScope(tc)
      return tc
    }

    infix operator fun String.invoke(test: TestContext.() -> Unit): TestCase {
      val tc = TestCase(context.currentScope().description().append("should " + this), this@AbstractSpec, test, lineNumber(), defaultTestCaseConfig)
      context.executeScope(tc)
      return tc
    }
  }

  inner class RootTestBuilder(val name: String) {

    fun add(test: TestContext.() -> Unit, config: TestCaseConfig) {
      val tc = TestCase(root().description().append(name), this@AbstractSpec, test, lineNumber(), config)
      addRootScope(tc)
    }

    infix operator fun invoke(test: TestContext.() -> Unit) = add(test, defaultTestCaseConfig)

    fun config(
        invocations: Int? = null,
        enabled: Boolean? = null,
        timeout: Duration? = null,
        threads: Int? = null,
        tags: Set<Tag>? = null,
        extensions: List<TestCaseExtension>? = null,
        test: TestContext.() -> Unit) {
      val config = TestCaseConfig(
          enabled ?: defaultTestCaseConfig.enabled,
          invocations ?: defaultTestCaseConfig.invocations,
          timeout ?: defaultTestCaseConfig.timeout,
          threads ?: defaultTestCaseConfig.threads,
          tags ?: defaultTestCaseConfig.tags,
          extensions ?: defaultTestCaseConfig.extensions)
      add(test, config)
    }
  }

  inner class TestBuilder(val context: TestContext, val name: String) {

    fun add(test: TestContext.() -> Unit, config: TestCaseConfig) {
      val tc = TestCase(context.currentScope().description().append(name), this@AbstractSpec, test, lineNumber(), config)
      context.executeScope(tc)
    }

    infix operator fun invoke(test: TestContext.() -> Unit) = add(test, defaultTestCaseConfig)

    fun config(
        invocations: Int? = null,
        enabled: Boolean? = null,
        timeout: Duration? = null,
        threads: Int? = null,
        tags: Set<Tag>? = null,
        extensions: List<TestCaseExtension>? = null,
        test: TestContext.() -> Unit) {
      val config = TestCaseConfig(
          enabled ?: defaultTestCaseConfig.enabled,
          invocations ?: defaultTestCaseConfig.invocations,
          timeout ?: defaultTestCaseConfig.timeout,
          threads ?: defaultTestCaseConfig.threads,
          tags ?: defaultTestCaseConfig.tags,
          extensions ?: defaultTestCaseConfig.extensions)
      add(test, config)
    }
  }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DisplayName(val name: String)