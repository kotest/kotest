package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * Returns an [Arb] which repeatedly generates a single value.
 */
fun <A> Arb.Companion.constant(a: A) = element(a)
