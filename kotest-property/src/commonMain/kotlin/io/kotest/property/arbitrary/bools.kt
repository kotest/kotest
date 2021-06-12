package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen

/**
 * Returns an [Arb] that produces [Boolean]s.
 */
fun Arb.Companion.boolean(): Arb<Boolean> = arbitrary(listOf(true, false)) { it.random.nextBoolean() }

@Deprecated("use boolean", ReplaceWith("boolean()"))
fun Arb.Companion.bool(): Arb<Boolean> = boolean()

/**
 * Returns an [Arb] that produces [BooleanArray]s where [length] produces the length of the arrays and
 * [content] produces the content of the arrays.
 */
fun Arb.Companion.booleanArray(length: Gen<Int>, content: Arb<Boolean>): Arb<BooleanArray> =
   toPrimitiveArray(length, content, Collection<Boolean>::toBooleanArray)
