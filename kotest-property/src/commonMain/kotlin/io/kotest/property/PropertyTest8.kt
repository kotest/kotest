@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.default
import io.kotest.property.internal.proptest

suspend fun <A, B, C, D, E, F, G, H> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Unit
): PropertyContext = proptest(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG), genA, genB, genC, genD, genE, genF, genG, genH, PropTestConfig(), property)

suspend fun <A, B, C, D, E, F, G, H> checkAll(
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Unit
): PropertyContext = checkAll(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG), config, genA, genB, genC, genD, genE, genF, genG, genH, property)

suspend fun <A, B, C, D, E, F, G, H> checkAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Unit
): PropertyContext = proptest(iterations, genA, genB, genC, genD, genE, genF, genG, genH, PropTestConfig(), property)

suspend fun <A, B, C, D, E, F, G, H> checkAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Unit
): PropertyContext = proptest(iterations, genA, genB, genC, genD, genE, genF, genG, genH, config, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H> checkAll(
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Unit
) = proptest(
   PropertyTesting.defaultIterationCount,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Unit
) = proptest(
   PropertyTesting.defaultIterationCount,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config,
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Unit
) = proptest(
   iterations,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Unit
) = proptest(
   iterations,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config,
   property
)

suspend fun <A, B, C, D, E, F, G, H> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
) = forAll(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG), PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, property)

suspend fun <A, B, C, D, E, F, G, H> forAll(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
) = forAll(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG), config, genA, genB, genC, genD, genE, genF, genG, genH, property)

suspend fun <A, B, C, D, E, F, G, H> forAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
) = forAll(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, property)

suspend fun <A, B, C, D, E, F, G, H> forAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
) = proptest(iterations, genA, genB, genC, genD, genE, genF, genG, genH, config) { a, b, c, d, E, F, G, H -> property(a, b, c, d, E, F, G, H) shouldBe true }

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H> forAll(
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
): PropertyContext = forAll(PropertyTesting.defaultIterationCount, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
): PropertyContext = forAll(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
) = forAll(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
) = proptest<A, B, C, D, E, F, G, H>(
   iterations,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config
) { a, b, c, d, e, f, g, h -> property(a, b, c, d, e, f, g, h) shouldBe true }

suspend fun <A, B, C, D, E, F, G, H> forNone(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
) = forNone(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG), PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, property)

suspend fun <A, B, C, D, E, F, G, H> forNone(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
) = forNone(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG), config, genA, genB, genC, genD, genE, genF, genG, genH, property)

suspend fun <A, B, C, D, E, F, G, H> forNone(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
) = forNone(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, property)

suspend fun <A, B, C, D, E, F, G, H> forNone(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
) = proptest(iterations, genA, genB, genC, genD, genE, genF, genG, genH, config) {
   a, b, c, d, E, F, G, h ->
   property(a, b, c, d, E, F, G, h) shouldBe false
}

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H> forNone(
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
): PropertyContext = forNone(PropertyTesting.defaultIterationCount, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
): PropertyContext = forNone(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
) = forNone(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H) -> Boolean
) = proptest<A, B, C, D, E, F, G, H>(
   iterations,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config
) { a, b, c, d, E, F, G, H -> property(a, b, c, d, E, F, G, H) shouldBe false }
