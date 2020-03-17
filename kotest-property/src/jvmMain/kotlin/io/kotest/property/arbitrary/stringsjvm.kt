package io.kotest.property.arbitrary

import com.mifmif.common.regex.Generex
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import kotlin.random.asJavaRandom

/**
 * Generate strings that match the given pattern.
 *
 * The returned arb uses the [Generex](https://github.com/mifmif/Generex) library to generate strings. Generex
 * supports a very restricted subset of regular expression constructs.
 */
fun Arb.Companion.stringPattern(pattern: String) = object : Arb<String>() {
   override fun edgecases(): List<String> = emptyList()

   override fun values(rs: RandomSource): Sequence<Sample<String>> {
      val generex = Generex(pattern, rs.random.asJavaRandom())

      return generateSequence { Sample(generex.random()) }
   }
}
