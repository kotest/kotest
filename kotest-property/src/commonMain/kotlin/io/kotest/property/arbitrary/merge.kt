package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.RandomSource
import io.kotest.property.Sample

/**
 * Returns a new [Arb] which will merge the values from this Arb and the values of
 * the supplied gen together, taking one from each in turn.
 *
 * In other words, if genA provides 1,2,3 and genB provides 7,8,9 then the merged
 * gen would output 1,7,2,8,3,9.
 *
 * The supplied gen must be a subtype of the type of this gen.
 *
 * Edgecases are generated from both inputs.
 *
 * @param other the arg to merge with this one
 * @return the merged arg.
 */
fun <A, B : A> Arb<A>.merge(other: Gen<B>): Arb<A> = object : Arb<A>() {

   override fun edgeCase(rs: RandomSource): A? = when (other) {
      is Arb -> listOf(this@merge, other).random(rs.random).edgeCase(rs)
      is Exhaustive -> this@merge.edgeCase(rs)
   }

   override fun sample(rs: RandomSource): Sample<A> =
      if (rs.random.nextBoolean()) {
         this@merge.sample(rs)
      } else {
         when (other) {
            is Arb -> other.sample(rs)
            is Exhaustive -> other.toArb().sample(rs)
         }
      }
}
