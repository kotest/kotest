package io.kotest.property.arbitrary

import com.github.curiousoddman.rgxgen.RgxGen
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import java.util.Random

/**
 * Generate strings that match the given pattern.
 *
 * The returned arb uses the [RgxGen](https://github.com/curious-odd-man/RgxGen) library to generate strings.
 * RgxGen supports a restricted subset of regular expression constructs.
 */
fun Arb.Companion.stringPattern(pattern: String): Arb<String> = object : Arb<String>() {

   val rgxgen = RgxGen.parse(pattern)

   override fun edgecase(rs: RandomSource): Sample<String>? = null
   override fun sample(rs: RandomSource): Sample<String> = sampleStringPattern(rs)

   private fun sampleStringPattern(rs: RandomSource): Sample<String> = synchronized(this) {
      val value = rgxgen.generate(Random(rs.random.nextLong()))
      Sample(value)
   }
}
