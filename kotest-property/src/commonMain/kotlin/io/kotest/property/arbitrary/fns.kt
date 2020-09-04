package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample

/**
 * Returns an [Arb] where each value is generated from the given function.
 */
fun <A> Arb.Companion.create(edgeCases: List<A> = emptyList(), fn: (RandomSource) -> A): Arb<A> = object : Arb<A>() {
   override fun value(rs: RandomSource): Sample<A> = Sample(fn(rs))
   override fun values(rs: RandomSource): Sequence<Sample<A>> = generateSequence { Sample(fn(rs)) }
   override fun edgecases(): List<A> = edgeCases
}
