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
): PropertyContext = checkAll(1000, genA, config, property)

suspend fun <A> checkAll(
   iterations: Int,
   genA: Gen<A>,
   config: PropTestConfig = PropTestConfig(),
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext = proptest(iterations, genA, config, property)

suspend inline fun <reified A> checkAll(
   config: PropTestConfig = PropTestConfig(),
   noinline property: suspend PropertyContext.(A) -> Unit
): PropertyContext = checkAll(1000, config, property)

suspend inline fun <reified A> checkAll(
   iterations: Int,
   config: PropTestConfig = PropTestConfig(),
   noinline property: suspend PropertyContext.(A) -> Unit
): PropertyContext = proptest(
   iterations,
   Arb.default<A>(),
   config,
   property
)

suspend fun <A> forAll(
   genA: Gen<A>,
   config: PropTestConfig = PropTestConfig(),
   property: PropertyContext.(A) -> Boolean
) = forAll(1000, genA, config, property)

suspend fun <A> forAll(
   iterations: Int,
   genA: Gen<A>,
   config: PropTestConfig = PropTestConfig(),
   property: PropertyContext.(A) -> Boolean
) = proptest(iterations, genA, config) { a -> property(a).shouldBeTrue() }

suspend inline fun <reified A> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: suspend PropertyContext.(A) -> Boolean
) = forAll(1000, config, property)

suspend inline fun <reified A> forAll(
   iterations: Int,
   config: PropTestConfig = PropTestConfig(),
   crossinline property: suspend PropertyContext.(A) -> Boolean
) = proptest<A>(
   iterations,
   Arb.default<A>(),
   config
) { a -> property(a).shouldBeTrue() }
