package io.kotest.property.arbitrary

import com.mifmif.common.regex.Generex
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.sampleOf

/**
 * Generate strings that match the given pattern.
 *
 * The returned arb uses the [Generex](https://github.com/mifmif/Generex) library to generate strings. Generex
 * supports a very restricted subset of regular expression constructs.
 */
fun Arb.Companion.stringPattern(pattern: String): Arb<String> = object : Arb<String>() {

   override fun edgecase(rs: RandomSource): String? = null

   override fun values(rs: RandomSource): Sequence<Sample<String>> = sequence { sampleStringPattern(rs) }
   override fun sample(rs: RandomSource): Sample<String> = sampleStringPattern(rs)

   private val generex = Generex(pattern)
   private fun sampleStringPattern(rs: RandomSource): Sample<String> = synchronized(this) {
      generex.setSeed(rs.random.nextLong())
      sampleOf(generex.random(), StringShrinker)
   }
}
