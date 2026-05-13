package io.kotest.property.arbitrary

import community.flock.kotlinx.rgxgen.RgxGen
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import kotlin.random.Random

/**
 * Generate strings that match the given regex pattern.
 *
 * Backed by [community.flock.kotlinx.rgxgen.RgxGen] (a Kotlin Multiplatform port
 * of [RgxGen](https://github.com/curious-odd-man/RgxGen)) and therefore works on
 * every Kotest target. The library supports a restricted subset of regular
 * expression constructs.
 */
fun Arb.Companion.pattern(pattern: String): Arb<String> = object : Arb<String>() {

   val rgxgen = RgxGen.parse(pattern)

   override fun edgecase(rs: RandomSource): Sample<String>? = null
   override fun sample(rs: RandomSource): Sample<String> {
      val value = rgxgen.generate(Random(rs.random.nextLong()))
      return Sample(value)
   }
}
