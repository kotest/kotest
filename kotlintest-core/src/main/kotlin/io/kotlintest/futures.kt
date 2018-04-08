package io.kotlintest

import java.util.concurrent.CompletableFuture

/**
 * Allows callers to queue up futures with a callback that will be executed
 * when the future has completed.
 */
fun <T> TestContext.whenReady(f: CompletableFuture<T>, test: (T) -> Unit) {
  this.run {
    val t = f.get()
    test(t)
    this.arrive()
  }
}