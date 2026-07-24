@file:OptIn(ExperimentalKotest::class)

package io.kotest.property

import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.shouldBe
import io.kotest.property.internal.proptest
import io.kotest.property.resolution.default

@IgnorableReturnValue
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

@IgnorableReturnValue
suspend fun <A, B, C, D> checkAll(
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext =
   proptest(genA, genB, genC, genD, config, property)

@IgnorableReturnValue
suspend fun <A, B, C, D> checkAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext = checkAll(PropTestConfig(constraints = Constraints.iterations(iterations)), genA, genB, genC, genD, property)

@IgnorableReturnValue
suspend fun <A, B, C, D> checkAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext = checkAll(config.copy(iterations = iterations), genA, genB, genC, genD, property)

@IgnorableReturnValue
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

@IgnorableReturnValue
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

@IgnorableReturnValue
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

@IgnorableReturnValue
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

@IgnorableReturnValue
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

@IgnorableReturnValue
suspend fun <A, B, C, D> forAll(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = proptest<A, B, C, D>(genA, genB, genC, genD, config) { a, b, c, d -> property(a, b, c, d) shouldBe true }

@IgnorableReturnValue
suspend fun <A, B, C, D> forAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = forAll<A, B, C, D>(iterations, PropTestConfig(), genA, genB, genC, genD, property)

@IgnorableReturnValue
suspend fun <A, B, C, D> forAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = forAll(config.copy(iterations = iterations), genA, genB, genC, genD, property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D> forAll(
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext = forAll(PropTestConfig(), property)

@IgnorableReturnValue
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

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = forAll(iterations, PropTestConfig(), property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = forAll(config.copy(iterations = iterations), property)

@IgnorableReturnValue
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

@IgnorableReturnValue
suspend fun <A, B, C, D> forNone(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = proptest<A, B, C, D>(genA, genB, genC, genD, config) { a, b, c, d -> property(a, b, c, d) shouldBe false }

@IgnorableReturnValue
suspend fun <A, B, C, D> forNone(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = forNone<A, B, C, D>(iterations, PropTestConfig(), genA, genB, genC, genD, property)

@IgnorableReturnValue
suspend fun <A, B, C, D> forNone(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Boolean
) = forNone<A, B, C, D>(config.copy(iterations = iterations), genA, genB, genC, genD, property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D> forNone(
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
): PropertyContext = forNone(PropTestConfig(), property)

@IgnorableReturnValue
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

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = forNone(iterations, PropTestConfig(), property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D) -> Boolean
) = forNone(config.copy(iterations = iterations), property)
