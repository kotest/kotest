@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.default
import io.kotest.property.internal.proptest

suspend fun <A, B> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   config: PropTestConfig = PropTestConfig(),
   property: suspend PropertyContext.(A, B) -> Unit
): PropertyContext = proptest(genA, genB, config, property)

suspend inline fun <reified A, reified B> checkAll(
   iterations: Int = 1000,
   config: PropTestConfig = PropTestConfig(),
   noinline property: suspend PropertyContext.(A, B) -> Unit
) = proptest(
   Arb.default<A>().take(iterations / 2),
   Arb.default<B>().take(iterations / 2),
   config,
   property
)

suspend fun <A, B> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   config: PropTestConfig = PropTestConfig(),
   property: suspend PropertyContext.(A, B) -> Boolean
) = proptest(genA, genB, config) { a, b -> property(a, b).shouldBeTrue() }

suspend inline fun <reified A, reified B> forAll(
   iterations: Int = 1000,
   config: PropTestConfig = PropTestConfig(),
   noinline property: suspend PropertyContext.(A, B) -> Boolean
) = proptest(
   Arb.default<A>().take(iterations / 2),
   Arb.default<B>().take(iterations / 2),
   config
) { a, b -> property(a, b).shouldBeTrue() }
