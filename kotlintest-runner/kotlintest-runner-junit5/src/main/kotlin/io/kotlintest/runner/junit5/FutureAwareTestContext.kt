package io.kotlintest.runner.junit5

import io.kotlintest.TestCase
import io.kotlintest.TestContext
import io.kotlintest.TestScope
import java.util.concurrent.CompletableFuture

/**
 * An implementation of [TestContext] that provides a future
 * so that threads can wait on the outcome of asynchronous operations.
 */
abstract class FutureAwareTestContext : TestContext {

  // in a normal synchronous test, once the closure is complete, the test has completed
  // but if we have asychronous tests we need to wait until they have completed before
  // we can notify the runner that the test has suceeded or failed
  // this future is used to chain future based testing
  private var future: CompletableFuture<Throwable> = CompletableFuture.completedFuture(null)

  /**
   * Returns a Future that can be used by the runner to wait on this context
   * until the [TestScope] that is using this context has completed.
   *
   * In the case of a synchronous test, this future will be an immediately
   * completed future, otherwise in the case of asynchrous tests, it will wait
   * pending the outcome of those operations.
   *
   * Each time a user calls whenReady() another future is chained, and then the future
   * returned by this call will wait until all those futures have completed.
   */
  fun future() = future

  private val _metadata = mutableListOf<Any>()

  override fun withMetaData(meta: Any) {
    _metadata.add(meta)
  }

  override fun metaData(): List<Any> = _metadata.toList()

  override fun <T> whenReady(f: CompletableFuture<T>, test: (T) -> Unit) {
    val next = CompletableFuture<Throwable>()
    future = future.thenCompose { next }
    // when the future given to us by the user completes, we update
    // the future we are using to track the results of the test.
    f.whenComplete { value, throwable ->
      if (value != null) {
        try {
          test(value)
          next.complete(null)
        } catch (e: AssertionError) {
          next.complete(e)
        }
      } else if (throwable != null) {
        next.complete(throwable)
      }
    }
  }
}

class TestCaseContext(val testCase: TestCase) : FutureAwareTestContext() {
  override fun currentScope(): TestScope = testCase
  override fun addScope(scope: TestScope): TestScope = throw UnsupportedOperationException()
}

class AccumulatingTestContext(val scope: TestScope) : FutureAwareTestContext() {

  override fun currentScope(): TestScope = scope

  val scopes = mutableListOf<TestScope>()

  override fun addScope(scope: TestScope): TestScope {
    scopes.add(scope)
    return scope
  }
}

