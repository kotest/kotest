@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.resolution.default
import io.kotest.property.internal.proptest

suspend fun <A, B, C> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   property: suspend PropertyContext.(A, B, C) -> Unit
): PropertyContext = proptest(
   genA,
   genB,
   genC,
   PropTestConfig(),
   property
)

suspend fun <A, B, C> checkAll(
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   property: suspend PropertyContext.(A, B, C) -> Unit
): PropertyContext =
   proptest(genA, genB, genC, config, property)

suspend fun <A, B, C> checkAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   property: suspend PropertyContext.(A, B, C) -> Unit
): PropertyContext = proptest(genA, genB, genC, PropTestConfig(constraints = Constraints.iterations(iterations)), property)

suspend fun <A, B, C> checkAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   property: suspend PropertyContext.(A, B, C) -> Unit
): PropertyContext = proptest(genA, genB, genC, config.copy(iterations = iterations), property)

suspend inline fun <reified A, reified B, reified C> checkAll(
   noinline property: suspend PropertyContext.(A, B, C) -> Unit
) = proptest(
   Arb.default(),
   Arb.default(),
   Arb.default(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B, reified C> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C) -> Unit
) = proptest(
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config,
   property
)

suspend inline fun <reified A, reified B, reified C> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B, C) -> Unit
) = proptest(
   Arb.default(),
   Arb.default(),
   Arb.default(),
   PropTestConfig(constraints = Constraints.iterations(iterations)),
   property
)

suspend inline fun <reified A, reified B, reified C> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C) -> Unit
) = proptest(
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config.copy(iterations = iterations),
   property
)

suspend fun <A, B, C> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   property: suspend PropertyContext.(A, B, C) -> Boolean
) = forAll(
   PropTestConfig(),
   genA,
   genB,
   genC,
   property
)

suspend fun <A, B, C> forAll(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   property: suspend PropertyContext.(A, B, C) -> Boolean
) = proptest(genA, genB, genC, config) { a, b, c -> property(a, b, c) shouldBe true }

suspend fun <A, B, C> forAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   property: suspend PropertyContext.(A, B, C) -> Boolean
) = forAll(iterations, PropTestConfig(), genA, genB, genC, property)

suspend fun <A, B, C> forAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   property: suspend PropertyContext.(A, B, C) -> Boolean
) = forAll(config.copy(iterations = iterations), genA, genB, genC, property)

suspend inline fun <reified A, reified B, reified C> forAll(
   crossinline property: PropertyContext.(A, B, C) -> Boolean
): PropertyContext = forAll(PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C) -> Boolean
): PropertyContext = proptest<A, B, C>(
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config
) { a, b, c -> property(a, b, c) shouldBe true }

suspend inline fun <reified A, reified B, reified C> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C) -> Boolean
) = forAll(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C) -> Boolean
) = forAll(config.copy(iterations = iterations), property)

suspend fun <A, B, C> forNone(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   property: suspend PropertyContext.(A, B, C) -> Boolean
) = forNone(
   PropTestConfig(),
   genA,
   genB,
   genC,
   property
)

suspend fun <A, B, C> forNone(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   property: suspend PropertyContext.(A, B, C) -> Boolean
) = proptest(genA, genB, genC, config) { a, b, c -> property(a, b, c) shouldBe false }

suspend fun <A, B, C> forNone(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   property: suspend PropertyContext.(A, B, C) -> Boolean
) = forNone(iterations, PropTestConfig(), genA, genB, genC, property)

suspend fun <A, B, C> forNone(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   property: suspend PropertyContext.(A, B, C) -> Boolean
) = forNone(config.copy(iterations = iterations), genA, genB, genC, property)

suspend inline fun <reified A, reified B, reified C> forNone(
   crossinline property: PropertyContext.(A, B, C) -> Boolean
): PropertyContext = forNone(PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C) -> Boolean
): PropertyContext = proptest(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   config
) { a, b, c -> property(a, b, c) shouldBe false }

suspend inline fun <reified A, reified B, reified C> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C) -> Boolean
) = forNone(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C) -> Boolean
) = forNone(config.copy(iterations = iterations), property)
