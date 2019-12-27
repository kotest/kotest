package io.kotest.property

data class PropTestArgs(
   val seed: Long = 0,
   val minSuccess: Int = Int.MAX_VALUE,
   val maxFailure: Int = 0,
   val shrinking: ShrinkingMode = ShrinkingMode.Bounded(1000)
)
