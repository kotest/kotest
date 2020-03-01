package io.kotest.property.arbitrary

import com.mifmif.common.regex.Generex
import io.kotest.property.RandomSource
import io.kotest.property.Sample

fun Arb.Companion.regex(regex: String) = object : Arb<String> {
   override fun edgecases(): List<String> = emptyList()

   override fun samples(rs: RandomSource): Sequence<Sample<String>> {
      val generex = Generex(regex)
      generex.setSeed(rs.seed)

      return generateSequence { Sample(generex.random()) }
   }
}

fun Arb.Companion.regex(regex: Regex) = regex(regex.pattern)
