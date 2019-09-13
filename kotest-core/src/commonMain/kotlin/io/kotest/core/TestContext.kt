package io.kotest.core

import io.kotest.Description
import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestType
import io.kotest.core.specs.KotestDsl
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * A [TestContext] is used as the receiver of a closure that is associated with a [TestCase].
 * This allows the scope body to interact with the test engine, for instance, adding metadata
 * during a test, reporting that an error was raised, or notifying the discovery
 * of a nested scope.
 *
  * [TestContext] implements [CoroutineScope], which allows test closures to launch coroutines
 * with the [CoroutineContext] provided by the test engine.
 */
@KotestDsl
abstract class TestContext(override val coroutineContext: CoroutineContext) : CoroutineScope {

  infix operator fun String.invoke(test: suspend TestContext.() -> Unit) {
    throw Exception("Nested tests are not allowed to be defined here. Please see the documentation for the spec styles")
  }

  /**
   * Returns the [Description] of the [TestCase] that is attached to this [TestContext].
   */
  abstract fun description(): Description

  /**
   * Creates a new [TestCase] and then notifies the test runner of this nested test.
   */
  suspend fun registerTestCase(name: String, spec: Spec, test: suspend TestContext.() -> Unit, config: TestCaseConfig, type: TestType) {
    val tc = TestCase(description().append(name), spec, test, sourceRef(), type, config)
    registerTestCase(tc)
  }

  /**
   * Notifies the test runner about a nested [TestCase].
   */
  abstract suspend fun registerTestCase(testCase: TestCase)
}
