package io.kotest.assertions

actual fun replay(
   times: Int,
   threads: Int,
   before: (Int) -> Unit,
   after: (Int) -> Unit,
   action: (Int) -> Unit
) {
   require(threads == 1) { "Cannot run JS tests on multiple threads. Use the built in kotlin function repeat" }
   simpleRepeat(times, before, after, action)
}
