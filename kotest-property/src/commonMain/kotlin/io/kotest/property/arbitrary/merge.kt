package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.RandomSource
import io.kotest.property.Sample

/**
 * Returns a new [Arb] which will merge the values from this Arb and the values of
 * the supplied gen together randomly, with roughly equal distribution.
 *
 * In other words, if genA provides 1,2,3 and genB provides 7,8,9 then the merged
 * gen may output 1,7,2,8,9,3.
 *
 * The [other] gen must be a subtype of the type of the first Arb.
 *
 * Edgecases are generated from both inputs.
 *
 * @param other the arg to merge with this one
 * @return the merged arg.
 */
fun <A, B : A> Arb<A>.merge(other: Gen<B>): Arb<A> = trampoline { sampleA ->
   object : Arb<A>() {
      override fun edgecase(rs: RandomSource): Sample<A>? = when (other) {
         is Arb -> if (rs.random.nextBoolean()) sampleA else other.edgecase(rs)
         is Exhaustive -> sampleA
      }

      override fun sample(rs: RandomSource): Sample<A> =
         if (rs.random.nextBoolean()) {
            sampleA
         } else {
            when (other) {
               is Arb -> other.sample(rs)
               is Exhaustive -> other.toArb().sample(rs)
            }
         }
   }
}
