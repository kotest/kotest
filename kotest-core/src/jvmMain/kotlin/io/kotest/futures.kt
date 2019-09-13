package io.kotest

import java.util.concurrent.CompletableFuture

/**
 * Allows callers to queue up futures with a callback that will be executed
 * when the future has completed.
 */
fun <A> whenReady(f: CompletableFuture<A>, test: (A) -> Unit) {
  val a = f.get()
  test(a)
}