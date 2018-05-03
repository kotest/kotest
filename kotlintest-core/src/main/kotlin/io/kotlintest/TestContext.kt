package io.kotlintest

import java.util.concurrent.ConcurrentHashMap

/**
 * A [TestContext] is used as the receiver of a closure that is associated with a [TestScope].
 * This allows the scope body to interact with the test engine, for instance, adding metadata
 * during a test, reporting that an error was raised, or notifying the discovery
 * of a nested scope.
 */
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
   * Returns the [Description] of the current [TestScope].
   */
  abstract fun description(): Description

  /**
   * Creates a new [TestScope] as a child scope of the currently executing scope
   * and then notifies the test runner with the new instance.
   */
  fun registerTestScope(name: String, spec: Spec, test: TestContext.() -> Unit, config: TestCaseConfig) {
    val tc = TestScope(description().append(name), spec, test, lineNumber(), config)
    registerTestScope(tc)
  }

  /**
   * Notifies the test runner about a nested [TestScope].
   */
  abstract fun registerTestScope(scope: TestScope)
}