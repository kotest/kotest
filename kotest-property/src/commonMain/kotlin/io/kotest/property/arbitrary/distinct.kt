package io.kotest.property.arbitrary

import io.kotest.common.DelicateKotest
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample

@DelicateKotest
fun <A> Arb<A>.distinct(attempts: Int = 100) = object : Arb<A>() {

   private val seen = mutableSetOf<A?>()

   override fun edgecase(rs: RandomSource): Sample<A>? {
      var iterations = 0
      while (iterations < attempts) {
         val edgecase = this@distinct.edgecase(rs)
         if (edgecase == null || seen.add(edgecase.value)) return edgecase
         iterations++
      }
      return null
   }

   override fun sample(rs: RandomSource): Sample<A> {
      var iterations = 0
      return generateSequence {
         if (iterations++ < attempts) this@distinct.sample(rs) else null
      }.filter { seen.add(it.value) }.first()
   }

}
