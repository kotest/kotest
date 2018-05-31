package io.kotlintest

import io.kotlintest.specs.KotlinTestDsl
import java.util.concurrent.ConcurrentHashMap

/**
 * A [TestContext] is used as the receiver of a closure that is associated with a [TestCase].
 * This allows the scope body to interact with the test engine, for instance, adding metadata
 * during a test, reporting that an error was raised, or notifying the discovery
 * of a nested scope.
 */
@KotlinTestDsl
abstract class TestContext {

  // needs to be thread safe as a context can be shared amongst many executing instances of the same scope
  private val metadata = ConcurrentHashMap<String, Any?>()

  /**
   * Adds a value to this [TestContext] meta data.
   */
  fun putMetaData(key: String, value: Any?) {
    metadata[key] = value
  }

  /**
   * Returns all the metadata associated with this [TestContext]
   */
  fun metaData() = metadata.toMap()

  /**
   * Returns the [Description] of the current [TestCase].
   */
  abstract fun description(): Description

  /**
   * Creates a new [TestCase] as a child of the currently executing test
   * and then notifies the test runner with the new instance.
   */
  fun registerTestCase(name: String, spec: Spec, test: TestContext.() -> Unit, config: TestCaseConfig, type: TestType) {
    val tc = TestCase(description().append(name), spec, test, lineNumber(), type, config)
    registerTestCase(tc)
  }

  /**
   * Notifies the test runner about a nested [TestCase].
   */
  abstract fun registerTestCase(testCase: TestCase)
}