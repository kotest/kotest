package io.kotest.engine.concurrency

expect suspend fun replay(
   times: Int,
   threads: Int,
   action: suspend (Int) -> Unit
)
