package io.kotlintest.properties

import com.mifmif.common.regex.Generex

class RegexpGen(private val regex: String) : Gen<String> {

   override fun constants(): Iterable<String> = emptyList()

   override fun random(): Sequence<String> {
      val generex = Generex(regex)
      return generateSequence {
         generex.random()
      }
   }
}
