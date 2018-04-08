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
  fun withMetaData(meta: Any)

  /**
   * Returns all the metadata associated with this [TestContext]
   */
  fun metaData(): List<Any>

  /**
   * Notifies the framework that a nested [TestScope] has been discovered
   * during the execution of a scope.
   */
  fun addScope(scope: TestScope): TestScope

  fun run(fn: () -> Unit)

  fun arrive()

  /**
   * The [TestScope] associated with the current execution
   */
  fun currentScope(): TestScope

  /**
   * The [Description] of the executing [TestScope].
   */
  fun description(): Description
}