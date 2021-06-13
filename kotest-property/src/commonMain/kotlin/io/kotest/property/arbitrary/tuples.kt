package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * Returns a [Arb] where each value is a [Triple] generated
 * by a value from each of three supplied generators.
 *
 * Edge cases are provided as the cross product of the edge cases of the component arbs.
 *
 * If any component does not provide an edge case, then a random value is substituted.
 */
fun <A, B, C> Arb.Companion.triple(
   arbA: Arb<A>,
   arbB: Arb<B>,
   arbC: Arb<C>
): Arb<Triple<A, B, C>> = Arb.bind(arbA, arbB, arbC) { a, b, c -> Triple(a, b, c) }
