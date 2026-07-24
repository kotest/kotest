@file:OptIn(ExperimentalKotest::class)

package io.kotest.property

import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.shouldBe
import io.kotest.property.internal.proptest
import io.kotest.property.resolution.default

@IgnorableReturnValue
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
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, PropTestConfig(), property)

@IgnorableReturnValue
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
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, config, property)

@IgnorableReturnValue
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
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, PropTestConfig(constraints = Constraints.iterations(iterations)), property)

@IgnorableReturnValue
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
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, config.copy(iterations = iterations), property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> checkAll(
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Unit
) = proptest(
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

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Unit
) = proptest(
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

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Unit
) = proptest(
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
   PropTestConfig(constraints = Constraints.iterations(iterations)),
   property
)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Unit
) = proptest(
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
   config.copy(iterations = iterations),
   property
)

@IgnorableReturnValue
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
) = forAll(PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, property)

@IgnorableReturnValue
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
) = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, config) { a, b, c, d, E, F, G, H, I, J, K -> property(a, b, c, d, E, F, G, H, I, J, K) shouldBe true }

@IgnorableReturnValue
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

@IgnorableReturnValue
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
) = forAll(config.copy(iterations = iterations), genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forAll(
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
): PropertyContext = forAll(PropTestConfig(), property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
): PropertyContext = proptest<A, B, C, D, E, F, G, H, I, J, K>(
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

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = forAll(iterations, PropTestConfig(), property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = forAll(config.copy(iterations = iterations), property)

@IgnorableReturnValue
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
) = forNone(PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, property)

@IgnorableReturnValue
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
) = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, config) {
      a, b, c, d, E, F, G, H, I, J, K ->
   property(a, b, c, d, E, F, G, H, I, J, K) shouldBe false
}

@IgnorableReturnValue
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

@IgnorableReturnValue
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
) = forNone(config.copy(iterations = iterations), genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forNone(
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
): PropertyContext = forNone(PropTestConfig(), property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
): PropertyContext = proptest<A, B, C, D, E, F, G, H, I, J, K>(
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

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = forNone(iterations, PropTestConfig(), property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Boolean
) = forNone(config.copy(iterations = iterations), property)
