@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.default
import io.kotest.property.internal.proptest

suspend fun <A, B, C, D, E, F, G, H, I, J, K> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   genJ: Gen<J>,
   genK: Gen<K>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Unit
): PropertyContext = proptest(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG), genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, PropTestConfig(), property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K> checkAll(
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
   genJ: Gen<J>,
   genK: Gen<K>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Unit
): PropertyContext = checkAll(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG), config, genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K> checkAll(
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
   genJ: Gen<J>,
   genK: Gen<K>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Unit
): PropertyContext = proptest(iterations, genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, PropTestConfig(), property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K> checkAll(
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
   genJ: Gen<J>,
   genK: Gen<K>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Unit
): PropertyContext = proptest(iterations, genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, config, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> checkAll(
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Unit
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
   Arb.default(),
   Arb.default(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Unit
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
   Arb.default(),
   Arb.default(),
   config,
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Unit
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
   Arb.default(),
   Arb.default(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Unit
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
   Arb.default(),
   Arb.default(),
   config,
   property
)

suspend fun <A, B, C, D, E, F, G, H, I, J, K> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   genJ: Gen<J>,
   genK: Gen<K>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = forAll(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK), PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K> forAll(
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
   genJ: Gen<J>,
   genK: Gen<K>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = forAll(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK), config, genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K> forAll(
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
   genJ: Gen<J>,
   genK: Gen<K>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = forAll(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K> forAll(
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
   genJ: Gen<J>,
   genK: Gen<K>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = proptest(iterations, genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, config) { a, b, c, d, E, F, G, H, I, J, K -> property(a, b, c, d, E, F, G, H, I, J, K) shouldBe true }

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forAll(
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
): PropertyContext = forAll(PropertyTesting.defaultIterationCount, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
): PropertyContext = forAll(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = forAll(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = proptest<A, B, C, D, E, F, G, H, I, J, K>(
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
   Arb.default(),
   Arb.default(),
   config
) { a, b, c, d, e, F, G, H, I, J, K -> property(a, b, c, d, e, F, G, H, I, J, K) shouldBe true }

suspend fun <A, B, C, D, E, F, G, H, I, J, K> forNone(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   genJ: Gen<J>,
   genK: Gen<K>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = forNone(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK), PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K> forNone(
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
   genJ: Gen<J>,
   genK: Gen<K>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = forNone(computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK), config, genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K> forNone(
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
   genJ: Gen<J>,
   genK: Gen<K>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = forNone(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K> forNone(
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
   genJ: Gen<J>,
   genK: Gen<K>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = proptest(iterations, genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, config) {
   a, b, c, d, E, F, G, H, I, J, K ->
   property(a, b, c, d, E, F, G, H, I, J, K) shouldBe false
}

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forNone(
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
): PropertyContext = forNone(PropertyTesting.defaultIterationCount, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
): PropertyContext = forNone(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = forNone(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = proptest<A, B, C, D, E, F, G, H, I, J, K>(
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
   Arb.default(),
   Arb.default(),
   config
) { a, b, c, d, E, F, G, H, I, J, K -> property(a, b, c, d, E, F, G, H, I, J, K) shouldBe false }
