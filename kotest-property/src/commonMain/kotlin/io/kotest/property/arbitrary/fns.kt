package io.kotest.property.arbitrary

import io.kotest.property.Sample
import kotlin.random.Random

/**
 * Returns an [Arb] whose value is generated from the given function.
 */
fun <A> Arb.Companion.create(fn: (Random) -> A): Arb<A> = object : Arb<A> {
   override fun edgecases(): List<A> = emptyList()
   override fun sample(random: Random): Sample<A> = Sample(fn(random))
}


