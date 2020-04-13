package io.kotest.properties

import com.mifmif.common.regex.Generex

class RegexpGen(private val regex: String) : Gen<String> {

   override fun constants(): Iterable<String> = emptyList()

   override fun random(seed: Long?): Sequence<String> {
      val generex = Generex(regex)
      if (seed != null) generex.setSeed(seed)
      return generateSequence {
         generex.random()
      }
   }
}
