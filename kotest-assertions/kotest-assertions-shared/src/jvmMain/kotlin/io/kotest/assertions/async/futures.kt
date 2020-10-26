package io.kotest.assertions.async

import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture

/**
 * Allows callers to queue up futures with a callback that will be executed
 * when the future has completed.
 */
@Deprecated("Use the built in coroutine function .await() from the kotlinx-coroutines-jdk8 module. This function will be removed in 4.4")
suspend fun <A> CompletableFuture<A>.whenReady(test: (A) -> Unit) {
   val a = this.await()
   test(a)
}
