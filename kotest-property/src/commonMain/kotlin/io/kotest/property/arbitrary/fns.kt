package io.kotest.property.arbitrary

import io.kotest.property.RandomSource

/**
 * Returns an [Arb] whose value is generated from the given function.
 */
fun <A> Arb.Companion.create(fn: (RandomSource) -> A): Arb<A> = arb { fn(it) }


