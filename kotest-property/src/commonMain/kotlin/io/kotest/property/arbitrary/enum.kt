package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * Returns an [Arb] that produces values randomly from the constants of the supplied enum type.
 * Eg, val arb: Arb<Season> = Arb.enum<Season>()
 */
inline fun <reified T : Enum<T>> Arb.Companion.enum(): Arb<T> = Arb.of(*enumValues<T>())
