@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.default
import io.kotest.property.internal.proptest

suspend fun <A> checkAll(
   genA: Gen<A>,
   config: PropTestConfig = PropTestConfig(),
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext = proptest(genA, config, property)

suspend inline fun <reified A> checkAll(
   iterations: Int = 1000,
   config: PropTestConfig = PropTestConfig(),
   noinline property: suspend PropertyContext.(A) -> Unit
): PropertyContext = proptest(
   Arb.default<A>().take(iterations),
   config,
   property
)

suspend fun <A> forAll(
   genA: Gen<A>,
   config: PropTestConfig = PropTestConfig(),
   property: PropertyContext.(A) -> Boolean
) = proptest(genA, config) { a -> property(a).shouldBeTrue() }

suspend inline fun <reified A> forAll(
   iterations: Int = 1000,
   config: PropTestConfig = PropTestConfig(),
   crossinline property: suspend PropertyContext.(A) -> Boolean
) = proptest<A>(
   Arb.default<A>().take(iterations),
   config
) { a -> property(a).shouldBeTrue() }
