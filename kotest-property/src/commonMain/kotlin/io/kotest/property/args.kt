package io.kotest.property

data class PropTestConfig(
   val seed: Long = 0,
   val minSuccess: Int = Int.MAX_VALUE,
   val maxFailure: Int = 0
)
