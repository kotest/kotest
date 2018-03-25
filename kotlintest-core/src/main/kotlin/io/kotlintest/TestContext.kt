package io.kotlintest

import java.util.concurrent.CompletableFuture

/**
 * A [TestContext] is used as the receiver of a closure that represents a [TestScope]
 * so that functions inside the test scope can pass back events to the test runner.
 *
 * For example, asynchronous tests need to be queued and the runner needs to be informed
 * that even though the closure has completed, the test case is still pending. In addition, the
 * test runnner needs to know about any nested nested [TestScope]s that were discovered
 * when executing the closure.
 */
interface TestContext {

  /**
   * Notifies the framework that a nested [TestScope] has been discovered
   * during the execution of a scope.
   */
  fun addScope(scope: TestScope): TestScope

  /**
   * Notifies the framework that an executing scope has an asynchronous
   * operation pending.
   */
  fun <T> whenReady(f: CompletableFuture<T>, test: (T) -> Unit)
}

open class DelegatingTestContext(val context: TestContext) : TestContext {
  override fun addScope(scope: TestScope): TestScope = context.addScope(scope)
  override fun <T> whenReady(f: CompletableFuture<T>, test: (T) -> Unit) = context.whenReady(f, test)
}