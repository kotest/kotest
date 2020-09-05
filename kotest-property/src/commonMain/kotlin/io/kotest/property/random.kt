package io.kotest.property

import kotlin.random.Random

data class RandomSource(val random: Random, val seed: Long) {
   companion object {

      fun seeded(seed: Long): RandomSource = RandomSource(Random(seed), seed)

      val Default = lazy {
         val seed = Random.Default.nextLong()
         RandomSource(Random(seed), seed)
      }.value
   }
}

fun Long.random(): RandomSource = when (this) {
   0L -> RandomSource(Random(0), 0)
   else -> RandomSource(Random(this), this)
}
