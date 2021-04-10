package io.kotest.property

import kotlin.random.Random

data class RandomSource(val random: Random, val seed: Long) {
   companion object {

      fun seeded(seed: Long): RandomSource = RandomSource(Random(seed), seed)

      @Deprecated("has concurrency issues with native")
      val Default = lazy {
         val seed = Random.Default.nextLong()
         RandomSource(Random(seed), seed)
      }.value

      fun default(): RandomSource {
         val seed = Random.Default.nextLong()
         return RandomSource(Random(seed), seed)
      }
   }
}

fun Long.random(): RandomSource = when (this) {
   0L -> RandomSource(Random(0), 0)
   else -> RandomSource(Random(this), this)
}
