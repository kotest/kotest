package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.internal.proptest
import io.kotest.property.resolution.default

suspend fun <A, B, C, D, E, F> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
): PropertyContext = proptest<A, B, C, D, E, F>(genA, genB, genC, genD, genE, genF, PropTestConfig(), property)

suspend fun <A, B, C, D, E, F> checkAll(
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, config, property)

suspend fun <A, B, C, D, E, F> checkAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, PropTestConfig(constraints = Constraints.iterations(iterations)), property)

suspend fun <A, B, C, D, E, F> checkAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, config.copy(iterations = iterations), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F> checkAll(
   noinline property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
) = proptest(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   Arb.default<E>(),
   Arb.default<F>(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
) = proptest<A, B, C, D, E, F>(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   Arb.default<E>(),
   Arb.default<F>(),
   config,
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
) = proptest<A, B, C, D, E, F>(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   Arb.default<E>(),
   Arb.default<F>(),
   PropTestConfig(constraints = Constraints.iterations(iterations)),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
) = proptest<A, B, C, D, E, F>(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   Arb.default<E>(),
   Arb.default<F>(),
   config.copy(iterations = iterations),
   property
)

suspend fun <A, B, C, D, E, F> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forAll<A, B, C, D, E, F>(PropTestConfig(), genA, genB, genC, genD, genE, genF, property)

suspend fun <A, B, C, D, E, F> forAll(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = proptest<A, B, C, D, E, F>(genA, genB, genC, genD, genE, genF, config) { a, b, c, d, e, f -> property(a, b, c, d, e, f) shouldBe true }

suspend fun <A, B, C, D, E, F> forAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forAll<A, B, C, D, E, F>(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, property)

suspend fun <A, B, C, D, E, F> forAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forAll<A, B, C, D, E, F>(config.copy(iterations = iterations), genA, genB, genC, genD, genE, genF, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forAll(
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
): PropertyContext = forAll<A, B, C, D, E, F>(PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
): PropertyContext = proptest<A, B, C, D, E, F>(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   Arb.default<E>(),
   Arb.default<F>(),
   config
) { a, b, c, d, e, f -> property(a, b, c, d, e, f) shouldBe true }

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forAll<A, B, C, D, E, F>(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forAll<A, B, C, D, E, F>(config.copy(iterations = iterations), property)

suspend fun <A, B, C, D, E, F> forNone(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forNone<A, B, C, D, E, F>(PropTestConfig(), genA, genB, genC, genD, genE, genF, property)

suspend fun <A, B, C, D, E, F> forNone(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = proptest<A, B, C, D, E, F>(genA, genB, genC, genD, genE, genF, config) {
      a, b, c, d, e, f ->
   property(a, b, c, d, e, f) shouldBe false
}

suspend fun <A, B, C, D, E, F> forNone(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forNone<A, B, C, D, E, F>(iterations, PropTestConfig(), genA, genB, genC, genD, genE, genF, property)

suspend fun <A, B, C, D, E, F> forNone(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forNone<A, B, C, D, E, F>(config.copy(iterations = iterations), genA, genB, genC, genD, genE, genF, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forNone(
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
): PropertyContext = forNone<A, B, C, D, E, F>(PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
): PropertyContext = proptest<A, B, C, D, E, F>(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   Arb.default<E>(),
   Arb.default<F>(),
   config
) { a, b, c, d, e, f -> property(a, b, c, d, e, f) shouldBe false }

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forNone<A, B, C, D, E, F>(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E, reified F> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E, F) -> Boolean
) = forNone<A, B, C, D, E, F>(config.copy(iterations = iterations), property)
