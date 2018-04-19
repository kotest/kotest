package io.kotlintest

/**
 * A [TestContext] is used as the receiver of a closure that represents a [Scope]
 * so that functions inside the test scope can interact with the test runner
 * in a platform independent way.
 *
 * The context is used to store metadata associated with a test, notifying
 * the test runner about any nested nested [Scope]s that were discovered
 * when executing the closure, and allowing async operations.
 */
interface TestContext {

  /**
   * Adds a value to this [TestContext] meta data.
   */
  fun putMetaData(key: String, value: Any?)

  /**
   * Returns all the metadata associated with this [TestContext]
   */
  fun metaData(): Map<String, Any?>

  /**
   * Notifies the test runner that a nested [Scope] has been created
   * during the execution of this scope.
   *
   * @return the given scope to allow builder pattern
   */
  fun executeScope(scope: Scope): Scope

  fun registerAsync()

  fun arriveAsync()

  fun withError(t: Throwable)

  fun error(): Throwable?

  /**
   * The [Scope] associated with the current execution
   */
  fun currentScope(): Scope

  /**
   * The [Description] of the executing [Scope].
   */
  fun description(): Description
}