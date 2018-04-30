package io.kotlintest

import java.util.concurrent.Phaser
import java.util.concurrent.atomic.AtomicReference

/**
 * A [TestContext] is used as the receiver of a closure that represents a [TestScope]
 * so that functions inside the test scope can interact with the test runner
 * in a platform independent way.
 *
 * The context is used to store metadata associated with a test, notifying
 * the test runner about any nested nested [TestScope]s that were discovered
 * when executing the closure, and allowing async operations.
 */
abstract class TestContext {

  private val metadata = mutableMapOf<String, Any?>()

  /**
   * Adds a value to this [TestContext] meta data.
   */
  fun putMetaData(key: String, value: Any?) {
    metadata[key] = value
  }

  abstract fun description(): Description

  /**
   * Returns all the metadata associated with this [TestContext]
   */
  fun metaData() = metadata.toMap()

  /**
   * Creates a new [TestScope] as a child of the currently executing test
   * and then notifies the test runner with the new instance.
   */
  fun registerTestScope(name: String, spec: Spec, test: TestContext.() -> Unit, config: TestCaseConfig) {
    val tc = TestScope(description().append(name), spec, test, lineNumber(), config)
    registerTestScope(tc)
  }

  /**
   * Notifies the test runner about a nested [TestScope].
   */
  abstract fun registerTestScope(testScope: TestScope)

  private val phaser = Phaser()
  private val error = AtomicReference<Throwable?>(null)

  fun registerAsync() {
    phaser.register()
  }

  fun arriveAsync() {
    phaser.arrive()
  }

  fun blockUntilReady() {
    registerAsync()
    phaser.arriveAndAwaitAdvance()
  }

  fun withError(t: Throwable) {
    error.set(t)
  }

  fun error(): Throwable? = error.get()
}