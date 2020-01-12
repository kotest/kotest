package io.kotest

import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture

/**
 * Allows callers to queue up futures with a callback that will be executed
 * when the future has completed.
 */
@Deprecated("use the extension function variant: future.whenReady { } which uses coroutines rather than blocking")
fun <A> whenReady(f: CompletableFuture<A>, test: (A) -> Unit) {
   val a = f.get()
   test(a)
}

suspend fun <A> CompletableFuture<A>.whenReady(test: (A) -> Unit) {
   val a = this.await()
   test(a)
}
