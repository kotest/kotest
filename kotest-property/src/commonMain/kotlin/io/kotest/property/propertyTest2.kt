package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.internal.proptest
import io.kotest.property.resolution.default

suspend fun <A, B> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Unit
): PropertyContext = proptest(genA, genB, PropTestConfig(), property)

suspend fun <A, B> checkAll(
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Unit
): PropertyContext = proptest(genA, genB, config, property)

suspend fun <A, B> checkAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Unit
): PropertyContext = proptest(genA, genB, PropTestConfig(constraints = Constraints.iterations(iterations)), property)

suspend fun <A, B> checkAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Unit
): PropertyContext = proptest(genA, genB, config.copy(iterations = iterations), property)

suspend inline fun <reified A, reified B> checkAll(
   noinline property: suspend PropertyContext.(A, B) -> Unit
) = proptest(
   Arb.default(),
   Arb.default(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B> PropTest.checkAll(
   noinline property: suspend PropertyContext.(A, B) -> Unit
) = proptest(
   Arb.default(),
   Arb.default(),
   this.toPropTestConfig(),
   property
)

suspend inline fun <reified A, reified B> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B) -> Unit
) = proptest(
   Arb.default(),
   Arb.default(),
   config,
   property
)

suspend inline fun <reified A, reified B> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B) -> Unit
) = proptest(
   Arb.default(),
   Arb.default(),
   PropTestConfig(constraints = Constraints.iterations(iterations)),
   property
)

suspend inline fun <reified A, reified B> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B) -> Unit
) = proptest(
   Arb.default(),
   Arb.default(),
   config.copy(iterations = iterations),
   property
)

suspend fun <A, B> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Boolean
) = forAll(PropTestConfig(), genA, genB, property)

suspend fun <A, B> forAll(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Boolean
) = proptest(genA, genB, config) { a, b -> property(a, b) shouldBe true }

suspend fun <A, B> forAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Boolean
) = forAll(iterations, PropTestConfig(), genA, genB, property)

suspend fun <A, B> forAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Boolean
) = forAll(config.copy(iterations = iterations), genA, genB, property)

suspend inline fun <reified A, reified B> forAll(
   crossinline property: PropertyContext.(A, B) -> Boolean
): PropertyContext = forAll(PropTestConfig(), property)

suspend inline fun <reified A, reified B> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B) -> Boolean
): PropertyContext = proptest<A, B>(
   Arb.default(),
   Arb.default(),
   config,
) { a, b -> property(a, b) shouldBe true }

suspend inline fun <reified A, reified B> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B) -> Boolean
) = forAll(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B) -> Boolean
) = forAll(config.copy(iterations = iterations), property)

suspend fun <A, B> forNone(
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Boolean
) = forNone(PropTestConfig(), genA, genB, property)

suspend fun <A, B> forNone(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Boolean
) = proptest(genA, genB, config) { a, b -> property(a, b) shouldBe false }

suspend fun <A, B> forNone(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Boolean
) = forNone(iterations, PropTestConfig(), genA, genB, property)

suspend fun <A, B> forNone(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   property: suspend PropertyContext.(A, B) -> Boolean
) = forNone(config.copy(iterations = iterations), genA, genB, property)

suspend inline fun <reified A, reified B> forNone(
   crossinline property: PropertyContext.(A, B) -> Boolean
): PropertyContext = forNone(PropTestConfig(), property)

suspend inline fun <reified A, reified B> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B) -> Boolean
): PropertyContext = proptest<A, B>(
   Arb.default(),
   Arb.default(),
   config
) { a, b -> property(a, b) shouldBe false }

suspend inline fun <reified A, reified B> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B) -> Boolean
) = forNone(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B) -> Boolean
) = forNone(config.copy(iterations = iterations), property)
