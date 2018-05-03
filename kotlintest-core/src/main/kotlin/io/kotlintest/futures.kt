package io.kotlintest

import java.util.concurrent.CompletableFuture

/**
 * Allows callers to queue up futures with a callback that will be executed
 * when the future has completed.
 */
fun <T> TestContext.whenReady(f: CompletableFuture<T>, test: (T) -> Unit) {
  f.whenComplete { value, throwable ->
    try {
      if (throwable != null)
        throw throwable
      test(value)
    } catch (t: Throwable) {
    } finally {
    }
  }
}