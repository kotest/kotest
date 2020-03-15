package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RandomSource

/**
 * Returns an [Arb] where each value is generated from the given function.
 */
fun <A> Arb.Companion.create(edgeCases: List<A>, fn: (RandomSource) -> A): Arb<A> =
   arb(edgeCases) { sequence { yield(fn(it)) } }

fun <A> Arb.Companion.create(fn: (RandomSource) -> A): Arb<A> = arb { sequence { yield(fn(it)) } }


