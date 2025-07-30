package io.kotest.core.test

import io.kotest.engine.IterationSkippedException

// TestScope is added just to restrict where this function can be used.
// It is not used in the function itself.
@Suppress("UnusedReceiverParameter")
fun TestScope.runIf(thunk: () -> Boolean) {
   if (!thunk()) throw IterationSkippedException()
}
