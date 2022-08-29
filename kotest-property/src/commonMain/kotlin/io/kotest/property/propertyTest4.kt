@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.resolution.default
import io.kotest.property.internal.proptest

suspend fun <A, B, C, D> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext = proptest<A, B, C, D>(
   genA,
   genB,
   genC,
   genD,
   PropTestConfig(),
   property
)

suspend fun <A, B, C, D> checkAll(
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext =
   proptest(genA, genB, genC, genD, config, property)

suspend fun <A, B, C, D> checkAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext = checkAll(PropTestConfig(constraints = Constraints.iterations(iterations)), genA, genB, genC, genD, property)

suspend fun <A, B, C, D> checkAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext = checkAll(config.copy(iterations = iterations), genA, genB, genC, genD, property)

suspend inline fun <reified A, reified B, reified C, reified D> checkAll(
   noinline property: suspend PropertyContext.(A, B, C, D) -> Unit
) = proptest(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D) -> Unit
) = proptest<A, B, C, D>(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   config,
   property
)

suspend inline fun <reified A, reified B, reified C, reified D> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B, C, D) -> Unit
) = proptest(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   PropTestConfig(constraints = Constraints.iterations(iterations)),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D) -> Unit
) = proptest(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   config.copy(iterations = iterations),
   property
)

suspend fun <A, B, C, D> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = forAll<A, B, C, D>(
   PropTestConfig(),
   genA,
   genB,
   genC,
   genD,
   property
)

suspend fun <A, B, C, D> forAll(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = proptest<A, B, C, D>(genA, genB, genC, genD, config) { a, b, c, d -> property(a, b, c, d) shouldBe true }

suspend fun <A, B, C, D> forAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = forAll<A, B, C, D>(iterations, PropTestConfig(), genA, genB, genC, genD, property)

suspend fun <A, B, C, D> forAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = forAll(config.copy(iterations = iterations), genA, genB, genC, genD, property)

suspend inline fun <reified A, reified B, reified C, reified D> forAll(
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext = forAll(PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext = proptest(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   config
) { a, b, c, d -> property(a, b, c, d) shouldBe true }

suspend inline fun <reified A, reified B, reified C, reified D> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = forAll(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = forAll(config.copy(iterations = iterations), property)

suspend fun <A, B, C, D> forNone(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = forNone<A, B, C, D>(
   PropTestConfig(),
   genA,
   genB,
   genC,
   genD,
   property
)

suspend fun <A, B, C, D> forNone(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = proptest<A, B, C, D>(genA, genB, genC, genD, config) { a, b, c, d -> property(a, b, c, d) shouldBe false }

suspend fun <A, B, C, D> forNone(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = forNone<A, B, C, D>(iterations, PropTestConfig(), genA, genB, genC, genD, property)

suspend fun <A, B, C, D> forNone(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = forNone<A, B, C, D>(config.copy(iterations = iterations), genA, genB, genC, genD, property)

suspend inline fun <reified A, reified B, reified C, reified D> forNone(
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext = forNone(PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext = proptest(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   config
) { a, b, c, d -> property(a, b, c, d) shouldBe false }

suspend inline fun <reified A, reified B, reified C, reified D> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = forNone(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = forNone(config.copy(iterations = iterations), property)
