package io.kotest.mpp

expect suspend fun replay(
   times: Int,
   threads: Int,
   action: suspend (Int) -> Unit
)
