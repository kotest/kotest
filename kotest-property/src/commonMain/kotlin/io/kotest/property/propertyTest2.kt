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
): PropertyContext = checkAll(1000, genA, genB, config, property)

suspend fun <A, B> checkAll(
   iterations: Int = 1000,
   genA: Gen<A>,
   genB: Gen<B>,
   config: PropTestConfig = PropTestConfig(),
   property: suspend PropertyContext.(A, B) -> Unit
): PropertyContext = proptest(iterations, genA, genB, config, property)

suspend inline fun <reified A, reified B> checkAll(
   iterations: Int = 1000,
   config: PropTestConfig = PropTestConfig(),
   noinline property: suspend PropertyContext.(A, B) -> Unit
) = proptest(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   config,
   property
)

suspend fun <A, B> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   config: PropTestConfig = PropTestConfig(),
   property: suspend PropertyContext.(A, B) -> Boolean
) = forAll(1000, genA, genB, config, property)

suspend fun <A, B> forAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   config: PropTestConfig = PropTestConfig(),
   property: suspend PropertyContext.(A, B) -> Boolean
) = proptest(iterations, genA, genB, config) { a, b -> property(a, b).shouldBeTrue() }

suspend inline fun <reified A, reified B> forAll(
   config: PropTestConfig = PropTestConfig(),
   noinline property: suspend PropertyContext.(A, B) -> Boolean
) = forAll(1000, config, property)

suspend inline fun <reified A, reified B> forAll(
   iterations: Int = 1000,
   config: PropTestConfig = PropTestConfig(),
   noinline property: suspend PropertyContext.(A, B) -> Boolean
) = proptest(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   config
) { a, b -> property(a, b).shouldBeTrue() }
