@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.PropertyTesting.computeDefaultIteration
import io.kotest.property.arbitrary.default
import io.kotest.property.internal.proptest

suspend fun <A, B, C, D, E, F, G, H, I> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Unit
): PropertyContext = proptest(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG), genA, genB, genC, genD, genE, genF, genG, genH, genI,  PropTestConfig(), property)

suspend fun <A, B, C, D, E, F, G, H, I> checkAll(
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Unit
): PropertyContext = checkAll(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG), config, genA, genB, genC, genD, genE, genF, genG, genH, genI,  property)

suspend fun <A, B, C, D, E, F, G, H, I> checkAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Unit
): PropertyContext = proptest(iterations, genA, genB, genC, genD, genE, genF, genG, genH, genI,  PropTestConfig(), property)

suspend fun <A, B, C, D, E, F, G, H, I> checkAll(
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
   genI: Gen<I>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Unit
): PropertyContext = proptest(iterations, genA, genB, genC, genD, genE, genF, genG, genH, genI,  config, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> checkAll(
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Unit
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
   Arb.default(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Unit
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
   Arb.default(),
   config,
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Unit
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
   Arb.default(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Unit
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
   Arb.default(),
   config,
   property
)

suspend fun <A, B, C, D, E, F, G, H, I> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = forAll(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG), PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI,  property)

suspend fun <A, B, C, D, E, F, G, H, I> forAll(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = forAll(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG), config, genA, genB, genC, genD, genE, genF, genG, genH, genI,  property)

suspend fun <A, B, C, D, E, F, G, H, I> forAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = forAll(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI,  property)

suspend fun <A, B, C, D, E, F, G, H, I> forAll(
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
   genI: Gen<I>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = proptest(iterations, genA, genB, genC, genD, genE, genF, genG, genH, genI,  config) { a, b, c, d, E, F, G, H, I -> property(a, b, c, d, E, F, G, H, I) shouldBe true }

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forAll(
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
): PropertyContext = forAll(PropertyTesting.defaultIterationCount, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
): PropertyContext = forAll(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = forAll(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = proptest<A, B, C, D, E, F, G, H, I>(
   iterations,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config
) { a, b, c, d, e, F, G, H, I -> property(a, b, c, d, e, F, G, H, I) shouldBe true }

suspend fun <A, B, C, D, E, F, G, H, I> forNone(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = forNone(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG), PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI,  property)

suspend fun <A, B, C, D, E, F, G, H, I> forNone(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = forNone(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG), config, genA, genB, genC, genD, genE, genF, genG, genH, genI,  property)

suspend fun <A, B, C, D, E, F, G, H, I> forNone(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = forNone(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI,  property)

suspend fun <A, B, C, D, E, F, G, H, I> forNone(
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
   genI: Gen<I>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = proptest(iterations, genA, genB, genC, genD, genE, genF, genG, genH, genI,  config) {
   a, b, c, d, E, F, G, H, I ->
   property(a, b, c, d, E, F, G, H, I) shouldBe false
}

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forNone(
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
): PropertyContext = forNone(PropertyTesting.defaultIterationCount, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
): PropertyContext = forNone(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = forNone(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = proptest<A, B, C, D, E, F, G, H, I>(
   iterations,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config
) { a, b, c, d, E, F, G, H, I -> property(a, b, c, d, E, F, G, H, I) shouldBe false }
