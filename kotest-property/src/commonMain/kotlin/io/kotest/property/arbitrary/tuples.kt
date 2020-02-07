package io.kotest.property.arbitrary

import io.kotest.property.RandomSource
import io.kotest.property.Sample

/**
 * Returns a [Arb] where each value is a [Triple] generated
 * by a value from each of three supplied generators.
 */
fun <A, B, C> Arb.Companion.triple(
   arbA: Arb<A>,
   arbB: Arb<B>,
   arbC: Arb<C>
): Arb<Triple<A, B, C>> = object : Arb<Triple<A, B, C>> {

   override fun edgecases(): List<Triple<A, B, C>> {
      return arbA.edgecases().zip(arbB.edgecases()).zip(arbC.edgecases()).map {
         Triple(
            it.first.first,
            it.first.second,
            it.second
         )
      }
   }

   override fun sample(rs: RandomSource): Sample<Triple<A, B, C>> {
      val a = arbA.sample(rs)
      val b = arbB.sample(rs)
      val c = arbC.sample(rs)
      return Sample(
         Triple(
            a.value,
            b.value,
            c.value
         )
      )
   }
}
