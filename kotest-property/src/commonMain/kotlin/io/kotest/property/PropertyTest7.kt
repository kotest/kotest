@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.resolution.default
import io.kotest.property.internal.proptest

suspend fun <A, B, C, D, E, F, G> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Unit
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, PropTestConfig(), property)

suspend fun <A, B, C, D, E, F, G> checkAll(
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Unit
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, config, property)

suspend fun <A, B, C, D, E, F, G> checkAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Unit
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, PropTestConfig(constraints = Constraints.iterations(iterations)), property)

suspend fun <A, B, C, D, E, F, G> checkAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Unit
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, config.copy(iterations = iterations), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> checkAll(
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Unit
) = proptest(
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

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Unit
) = proptest(
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

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Unit
) = proptest(
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

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Unit
) = proptest(
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

suspend fun <A, B, C, D, E, F,G> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Boolean
) = forAll(PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, property)

suspend fun <A, B, C, D, E, F, G> forAll(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Boolean
) = proptest(genA, genB, genC, genD, genE, genF, genG, config) { a, b, c, d, e, f, g -> property(a, b, c, d, e, f, g) shouldBe true }

suspend fun <A, B, C, D, E, F, G> forAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Boolean
) = forAll(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, property)

suspend fun <A, B, C, D, E, F, G> forAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Boolean
) = forAll(config.copy(iterations = iterations), genA, genB, genC, genD, genE, genF, genG, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> forAll(
   crossinline property: PropertyContext.(A, B, C, D, E, F, G) -> Boolean
): PropertyContext = forAll(PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F, G) -> Boolean
): PropertyContext = proptest<A, B, C, D, E, F, G>(
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config
) { a, b, c, d, e, f, g -> property(a, b, c, d, e, f, g) shouldBe true }

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G) -> Boolean
) = forAll(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G) -> Boolean
) = forAll(config.copy(iterations = iterations), property)

suspend fun <A, B, C, D, E, F, G> forNone(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Boolean
) = forNone(PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, property)

suspend fun <A, B, C, D, E, F, G> forNone(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Boolean
) = proptest(genA, genB, genC, genD, genE, genF, genG, config) {
      a, b, c, d, e, f, g ->
   property(a, b, c, d, e, f, g) shouldBe false
}

suspend fun <A, B, C, D, E, F, G> forNone(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Boolean
) = forNone(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, property)

suspend fun <A, B, C, D, E, F, G> forNone(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Boolean
) = forNone(config.copy(iterations = iterations), genA, genB, genC, genD, genE, genF, genG, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> forNone(
   crossinline property: PropertyContext.(A, B, C, D, E, F, G) -> Boolean
): PropertyContext = forNone(PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F, G) -> Boolean
): PropertyContext = proptest<A, B, C, D, E, F, G>(
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config
) { a, b, c, d, e, f, g -> property(a, b, c, d, e, f, g) shouldBe false }

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G) -> Boolean
) = forNone(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G) -> Boolean
) = forNone(config.copy(iterations = iterations), property)
