package io.kotlintest.properties

import com.mifmif.common.regex.Generex
import kotlin.random.Random

class RegexpGen(private val regex: String) : Gen<String> {

   override fun constants(): Iterable<String> = emptyList()

   override fun random(random: Random?): Sequence<String> {
      val generex = Generex(regex)
      if (random != null) generex.setSeed(random.nextLong())
      return generateSequence {
         generex.random()
      }
   }
}
