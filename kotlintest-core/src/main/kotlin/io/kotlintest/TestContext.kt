package io.kotlintest

/**
 * A [TestContext] is used as the receiver of a closure that represents a [TestScope]
 * so that functions inside the test scope can interact with the test runner
 * in a platform independent way.
 *
 * The context is used to store metadata associated with a test, notifying
 * the test runner about any nested nested [TestScope]s that were discovered
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
   * Notifies the test runner that a nested [TestScope] has been created
   * during the execution of this scope.
   *
   * @return the given scope to allow builder pattern
   */
  fun executeScope(scope: TestScope): TestScope

  fun registerAsync()

  fun arriveAsync()

  fun withError(t: Throwable)

  fun error(): Throwable?

  /**
   * The [TestScope] associated with the current execution
   */
  fun currentScope(): TestScope

  /**
   * The [Description] of the executing [TestScope].
   */
  fun description(): Description
}