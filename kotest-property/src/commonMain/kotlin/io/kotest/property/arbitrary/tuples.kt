package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample

/**
 * Returns a [Arb] where each value is a [Triple] generated
 * by a value from each of three supplied generators.
 *
 * Edgecases are provided as the cross product of the edge cases of the component arbs.
 */
fun <A, B, C> Arb.Companion.triple(
   arbA: Arb<A>,
   arbB: Arb<B>,
   arbC: Arb<C>
): Arb<Triple<A, B, C>> = object : Arb<Triple<A, B, C>>() {

   override fun edgecases(): List<Triple<A, B, C>> = sequence {
      for (a in arbA.edgecases()) {
         for (b in arbB.edgecases()) {
            for (c in arbC.edgecases()) {
               yield(Triple(a, b, c))
            }
         }
      }
   }.toList()

   override fun values(rs: RandomSource): Sequence<Sample<Triple<A, B, C>>> {
      return arbA.values(rs).zip(arbB.values(rs)).zip(arbC.values(rs)).map {
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
