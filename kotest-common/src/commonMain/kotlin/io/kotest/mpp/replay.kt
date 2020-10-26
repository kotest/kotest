package io.kotest.mpp

expect suspend fun replay(
   times: Int,
   threads: Int,
   before: suspend (Int) -> Unit,
   after: suspend (Int) -> Unit ,
   action: suspend (Int) -> Unit
)
