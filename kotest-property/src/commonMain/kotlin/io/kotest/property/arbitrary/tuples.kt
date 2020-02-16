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

   override fun samples(rs: RandomSource): Sequence<Sample<Triple<A, B, C>>> {
      return arbA.samples(rs).zip(arbB.samples(rs)).zip(arbC.samples(rs)).map {
         Sample(
            Triple(
               it.first.first.value,
               it.first.second.value,
               it.second.value
            )
         )
      }
   }
}
