package io.kotest.engine.concurrency

expect suspend fun replay(
   times: Int,
   action: suspend (Int) -> Unit
)
