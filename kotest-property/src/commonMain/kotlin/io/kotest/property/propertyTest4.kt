@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.PropertyTesting.computeDefaultIteration
import io.kotest.property.arbitrary.default
import io.kotest.property.internal.proptest

suspend fun <A, B, C, D> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext = proptest<A, B, C, D>(
   computeDefaultIteration(genA, genB, genC, genD),
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
   checkAll<A, B, C, D>(computeDefaultIteration(genA, genB, genC, genD), config, genA, genB, genC, genD, property)

suspend fun <A, B, C, D> checkAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext = proptest<A, B, C, D>(iterations, genA, genB, genC, genD, PropTestConfig(), property)

suspend fun <A, B, C, D> checkAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext = proptest<A, B, C, D>(iterations, genA, genB, genC, genD, config, property)

suspend inline fun <reified A, reified B, reified C, reified D> checkAll(
   noinline property: suspend PropertyContext.(A, B, C, D) -> Unit
) = proptest<A, B, C, D>(
   PropertyTesting.defaultIterationCount,
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
   PropertyTesting.defaultIterationCount,
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
) = proptest<A, B, C, D>(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D) -> Unit
) = proptest<A, B, C, D>(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   config,
   property
)

suspend fun <A, B, C, D> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = forAll<A, B, C, D>(
   computeDefaultIteration(genA, genB, genC, genD),
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
) = forAll<A, B, C, D>(computeDefaultIteration(genA, genB, genC, genD), config, genA, genB, genC, genD, property)

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
) =
   proptest<A, B, C, D>(iterations, genA, genB, genC, genD, config) { a, b, c, d -> property(a, b, c, d) shouldBe true }

suspend inline fun <reified A, reified B, reified C, reified D> forAll(
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext = forAll(PropertyTesting.defaultIterationCount, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext = forAll(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A, reified B, reified C, reified D> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = forAll(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = proptest(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   config
) { a, b, c, d -> property(a, b, c, d) shouldBe true }

suspend fun <A, B, C, D> forNone(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = forNone<A, B, C, D>(
   computeDefaultIteration(genA, genB, genC, genD),
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
) = forNone<A, B, C, D>(computeDefaultIteration(genA, genB, genC, genD), config, genA, genB, genC, genD, property)

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
) =
   proptest<A, B, C, D>(iterations, genA, genB, genC, genD, config) { a, b, c, d -> property(a, b, c, d) shouldBe false }

suspend inline fun <reified A, reified B, reified C, reified D> forNone(
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext = forNone(PropertyTesting.defaultIterationCount, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext = forNone(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A, reified B, reified C, reified D> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = forNone(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = proptest(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   config
) { a, b, c, d -> property(a, b, c, d) shouldBe false }
