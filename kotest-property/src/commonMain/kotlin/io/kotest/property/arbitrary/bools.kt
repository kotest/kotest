package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen

/**
 * Returns an [Arb] that produces [Boolean]s.
 */
fun Arb.Companion.bool(): Arb<Boolean> = arbitrary(listOf(true, false)) { it.random.nextBoolean() }

/**
 * Returns an [Arb] that produces [BooleanArray]s where [generateArrayLength] produces the length of the arrays and
 * [generateContents] produces the content of the arrays.
 */
fun Arb.Companion.boolArray(generateArrayLength: Gen<Int>, generateContents: Arb<Boolean>): Arb<BooleanArray> =
   toPrimitiveArray(generateArrayLength, generateContents, Collection<Boolean>::toBooleanArray)
