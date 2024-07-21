package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.internal.proptest
import io.kotest.property.resolution.default

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
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI,  PropTestConfig(), property)

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
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, config, property)

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
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, PropTestConfig(constraints = Constraints.iterations(iterations)), property)

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
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, config.copy(iterations = iterations), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> checkAll(
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Unit
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
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Unit
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
   config,
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Unit
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
   PropTestConfig(constraints = Constraints.iterations(iterations)),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Unit
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
   config.copy(iterations = iterations),
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
) = forAll(PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI, property)

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
) = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, config) { a, b, c, d, E, F, G, H, I -> property(a, b, c, d, E, F, G, H, I) shouldBe true }

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
) = forAll(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI, property)

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
) = forAll(config.copy(iterations = iterations), genA, genB, genC, genD, genE, genF, genG, genH, genI, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forAll(
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
): PropertyContext = forAll(PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
): PropertyContext = proptest<A, B, C, D, E, F, G, H, I>(
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

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = forAll(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = forAll(config.copy(iterations = iterations), property)

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
) = forNone(PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI, property)

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
) = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, config) {
      a, b, c, d, E, F, G, H, I ->
   property(a, b, c, d, E, F, G, H, I) shouldBe false
}

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
) = forNone(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, genG, genH, genI, property)

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
) = forNone(config.copy(iterations = iterations), genA, genB, genC, genD, genE, genF, genG, genH, genI, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forNone(
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
): PropertyContext = forNone(PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
): PropertyContext = proptest<A, B, C, D, E, F, G, H, I>(
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

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = forNone(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G, reified H, reified I> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F, G, H, I) -> Boolean
) = forNone(config.copy(iterations = iterations), property)
