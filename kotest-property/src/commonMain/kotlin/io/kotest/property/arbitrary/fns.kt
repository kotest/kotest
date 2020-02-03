package io.kotest.property.arbitrary

import kotlin.random.Random

/**
 * Returns an [Arb] whose value is generated from the given function.
 */
fun <A> Arb.Companion.create(fn: (Random) -> A): Arb<A> = arb { fn(it) }


