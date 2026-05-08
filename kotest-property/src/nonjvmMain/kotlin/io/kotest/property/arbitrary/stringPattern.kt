package io.kotest.property.arbitrary

import community.flock.kotlinx.rgxgen.RgxGen
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import kotlin.random.Random

actual fun Arb.Companion.stringPattern(pattern: String): Arb<String> = object : Arb<String>() {

   val rgxgen = RgxGen.parse(pattern)

   override fun edgecase(rs: RandomSource): Sample<String>? = null
   override fun sample(rs: RandomSource): Sample<String> {
      val value = rgxgen.generate(Random(rs.random.nextLong()))
      return Sample(value)
   }
}
