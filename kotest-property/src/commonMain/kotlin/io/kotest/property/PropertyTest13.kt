@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.resolution.default
import io.kotest.property.internal.proptest

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> checkAll(
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
   genL: Gen<L>,
   genM: Gen<M>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, PropTestConfig(), property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> checkAll(
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
   genL: Gen<L>,
   genM: Gen<M>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, config, property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> checkAll(
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
   genL: Gen<L>,
   genM: Gen<M>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, PropTestConfig(constraints = Constraints.iterations(iterations)), property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> checkAll(
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
   genL: Gen<L>,
   genM: Gen<M>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, config.copy(iterations = iterations), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K, reified L, reified M> checkAll(
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit
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
   Arb.default(),
   Arb.default(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K, reified L, reified M> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit
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
   Arb.default(),
   Arb.default(),
   config,
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K, reified L, reified M> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit
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
   Arb.default(),
   Arb.default(),
   PropTestConfig(constraints = Constraints.iterations(iterations)),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K, reified L, reified M> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit
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
   Arb.default(),
   Arb.default(),
   config.copy(iterations = iterations),
   property
)

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forAll(
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
   genL: Gen<L>,
   genM: Gen<M>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
) = forAll(PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forAll(
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
   genL: Gen<L>,
   genM: Gen<M>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
) = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, config) { a, b, c, d, e, f, g, h, i, j, k, l, m -> property(a, b, c, d, e, f, g, h, i, j, k, l, m) shouldBe true }

//suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forAll(
//   iterations: Int,
//   genA: Gen<A>,
//   genB: Gen<B>,
//   genC: Gen<C>,
//   genD: Gen<D>,
//   genE: Gen<E>,
//   genF: Gen<F>,
//   genG: Gen<G>,
//   genH: Gen<H>,
//   genI: Gen<I>,
//   genJ: Gen<J>,
//   genK: Gen<K>,
//   genL: Gen<L>,
//   genM: Gen<M>,
//   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
//) = forAll(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, property)

//suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forAll(
//   iterations: Int,
//   config: PropTestConfig,
//   genA: Gen<A>,
//   genB: Gen<B>,
//   genC: Gen<C>,
//   genD: Gen<D>,
//   genE: Gen<E>,
//   genF: Gen<F>,
//   genG: Gen<G>,
//   genH: Gen<H>,
//   genI: Gen<I>,
//   genJ: Gen<J>,
//   genK: Gen<K>,
//   genL: Gen<L>,
//   genM: Gen<M>,
//   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
//) = forAll(config.copy(iterations = iterations), genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, property)

//suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K, reified L, reified M> forAll(
//   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
//): PropertyContext = forAll(PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K, reified L, reified M> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
): PropertyContext = proptest<A, B, C, D, E, F, G, H, I, J, K, L, M>(
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
   Arb.default(),
   Arb.default(),
   config
) { a, b, c, d, e, f, g, h, i, j, k, l, m -> property(a, b, c, d, e, f, g, h, i, j, k, l, m) shouldBe true }

//suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K, reified L, reified M> forAll(
//   iterations: Int,
//   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
//) = forAll(iterations, PropTestConfig(), property)

//suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K, reified L, reified M> forAll(
//   iterations: Int,
//   config: PropTestConfig,
//   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
//) = forAll(config.copy(iterations = iterations), property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forNone(
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
   genL: Gen<L>,
   genM: Gen<M>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
) = forNone(PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forNone(
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
   genL: Gen<L>,
   genM: Gen<M>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
) = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, config) {
      a, b, c, d, e, f, g, h, i, j, k, l, m ->
   property(a, b, c, d, e, f, g, h, i, j, k, l, m) shouldBe false
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forNone(
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
   genL: Gen<L>,
   genM: Gen<M>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
) = forNone(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forNone(
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
   genL: Gen<L>,
   genM: Gen<M>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
) = forNone(config.copy(iterations = iterations), genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K, reified L, reified M> forNone(
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
): PropertyContext = forNone(PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K, reified L, reified M> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
): PropertyContext = proptest<A, B, C, D, E, F, G, H, I, J, K, L, M>(
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
   Arb.default(),
   Arb.default(),
   config
) { a, b, c, d, e, f, g, h, i, j, k, l, m -> property(a, b, c, d, e, f, g, h, i, j, k, l, m) shouldBe false }

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K, reified L, reified M> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
) = forNone(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I, reified J, reified K, reified L, reified M> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
) = forNone(config.copy(iterations = iterations), property)
