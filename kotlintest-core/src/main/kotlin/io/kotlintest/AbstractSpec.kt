package io.kotlintest

import java.io.Closeable
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

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
  open val oneInstancePerTest = true

  // the root container for specs
  // specs should add intermediate containers to this
  internal val rootContainer = TestContainer(javaClass.simpleName)

  private val ids = AtomicInteger(0)
  protected fun nextId(): String = ids.incrementAndGet().toString()

  override fun root(): TestContainer = rootContainer

  fun name(): String {
    val displayName = AbstractSpec::class.annotations.find { it is DisplayName }
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
annotation class DisplayName(val name: String)