package io.kotest.property

import kotlin.random.Random

data class PropTestConfig(
   val seed: Long = 0,
   val minSuccess: Int = Int.MAX_VALUE,
   val maxFailure: Int = 0
)

fun Long.random() = when (this) {
   0L -> Random.Default
   else -> Random(this)
}
