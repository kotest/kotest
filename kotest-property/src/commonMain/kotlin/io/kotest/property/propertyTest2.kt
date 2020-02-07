@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.default
import io.kotest.property.internal.proptest

suspend fun <A, B> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Unit
): PropertyContext = proptest<A, B>(DefaultIterationsCount, genA, genB, PropTestConfig(), property)

suspend fun <A, B> checkAll(
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Unit
): PropertyContext = checkAll<A, B>(DefaultIterationsCount, config, genA, genB, property)

suspend fun <A, B> checkAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Unit
): PropertyContext = proptest<A, B>(iterations, genA, genB, PropTestConfig(), property)

suspend fun <A, B> checkAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Unit
): PropertyContext = proptest<A, B>(iterations, genA, genB, config, property)


suspend inline fun <reified A, reified B> checkAll(
   noinline property: suspend PropertyContext.(A, B) -> Unit
) = proptest<A, B>(
   DefaultIterationsCount,
   Arb.default<A>(),
   Arb.default<B>(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B) -> Unit
) = proptest<A, B>(
   DefaultIterationsCount,
   Arb.default<A>(),
   Arb.default<B>(),
   config,
   property
)

suspend inline fun <reified A, reified B> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B) -> Unit
) = proptest<A, B>(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B) -> Unit
) = proptest<A, B>(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   config,
   property
)


suspend fun <A, B> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Boolean
) = forAll<A, B>(DefaultIterationsCount, PropTestConfig(), genA, genB, property)

suspend fun <A, B> forAll(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Boolean
) = forAll<A, B>(DefaultIterationsCount, config, genA, genB, property)

suspend fun <A, B> forAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Boolean
) = forAll<A, B>(iterations, PropTestConfig(), genA, genB, property)

suspend fun <A, B> forAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Boolean
) = proptest<A, B>(iterations, genA, genB, config) { a, b -> property(a, b).shouldBeTrue() }


suspend inline fun <reified A, reified B> forAll(
   crossinline property: PropertyContext.(A, B) -> Boolean
) = forAll<A, B>(DefaultIterationsCount, PropTestConfig(), property)

suspend inline fun <reified A, reified B> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B) -> Boolean
) = forAll<A, B>(DefaultIterationsCount, config, property)

suspend inline fun <reified A, reified B> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B) -> Boolean
) = forAll<A, B>(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B) -> Boolean
) = proptest<A, B>(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   config
) { a, b -> property(a, b).shouldBeTrue() }
