package io.kotlintest

import io.kotlintest.specs.KotlinTestDsl
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * A [TestContext] is used as the receiver of a closure that is associated with a [TestCase].
 * This allows an individual test to interact with the test engine to register nested test cases.
 *
  * [TestContext] al oimplements [CoroutineScope], which allows test closures to launch coroutines
 * with the [CoroutineContext] provided by the test engine.
 */
@KotlinTestDsl
abstract class TestContext(override val coroutineContext: CoroutineContext) : CoroutineScope {

  infix operator fun String.invoke(test: suspend TestContext.() -> Unit) {
    throw RuntimeException("Nested tests are not allowed to be defined here. Please see the documentation for the spec styles")
  }

  /**
   * Returns the [Description] of the [TestCase] that is attached to this [TestContext].
   */
  abstract fun description(): Description

  /**
   * Creates a new nested [TestCase] and then notifies the test engine with that test.
   */
  suspend fun registerTestCase(name: String, spec: Spec, test: suspend TestContext.() -> Unit, config: TestCaseConfig, type: TestType) {
    val tc = TestCase(description().append(name), spec, test, sourceRef(), type, config)
    registerTestCase(tc)
  }

  /**
   * Notifies the test engine about a nested [TestCase].
   */
  abstract suspend fun registerTestCase(testCase: TestCase)
}
