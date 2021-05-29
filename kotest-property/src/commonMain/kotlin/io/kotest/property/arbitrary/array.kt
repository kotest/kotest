package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen

internal inline fun <P, A> Arb.Companion.toPrimitiveArray(
   generateArrayLength: Gen<Int>,
   generateContents: Arb<P>,
   crossinline toArray: Collection<P>.() -> A
): Arb<A> = when (generateArrayLength) {
   is Arb -> generateArrayLength
   is Exhaustive -> generateArrayLength.toArb()
}.flatMap { length -> Arb.list(generateContents, length..length) }.map { it.toArray() }
