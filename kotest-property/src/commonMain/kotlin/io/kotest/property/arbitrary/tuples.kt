package io.kotest.property.arbitrary

import io.kotest.property.Arb

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
): Arb<Triple<A, B, C>> {

   val edgecases = arbA.edgecases().flatMap { a ->
      arbB.edgecases().flatMap { b ->
         arbC.edgecases().map { c ->
            Triple(a, b, c)
         }
      }
   }

   return arbitrary(edgecases) {
      val a = arbA.sample(it)
      val b = arbB.sample(it)
      val c = arbC.sample(it)
      Triple(
         a.value,
         b.value,
         c.value
      )
   }
}
