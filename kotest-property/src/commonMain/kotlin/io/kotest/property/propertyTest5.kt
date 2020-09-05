@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.PropertyTesting.computeDefaultIteration
import io.kotest.property.arbitrary.default
import io.kotest.property.internal.proptest

suspend fun <A, B, C, D, E> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Unit
): PropertyContext = proptest(
   computeDefaultIteration(genA, genB, genC, genD, genE),
   genA,
   genB,
   genC,
   genD,
   genE,
   PropTestConfig(),
   property
)

suspend fun <A, B, C, D, E> checkAll(
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Unit
): PropertyContext = checkAll(
   computeDefaultIteration(genA, genB, genC, genD, genE),
   config,
   genA,
   genB,
   genC,
   genD,
   genE,
   property
)

suspend fun <A, B, C, D, E> checkAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Unit
): PropertyContext = proptest(iterations, genA, genB, genC, genD, genE, PropTestConfig(), property)

suspend fun <A, B, C, D, E> checkAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Unit
): PropertyContext = proptest(iterations, genA, genB, genC, genD, genE, config, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E> checkAll(
   noinline property: suspend PropertyContext.(A, B, C, D, E) -> Unit
) = proptest(
   PropertyTesting.defaultIterationCount,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E) -> Unit
) = proptest(
   PropertyTesting.defaultIterationCount,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config,
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B, C, D, E) -> Unit
) = proptest(
   iterations,
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A, reified B, reified C, reified D, reified E> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E) -> Unit
) = proptest<A, B, C, D, E>(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   Arb.default<E>(),
   config,
   property
)

suspend fun <A, B, C, D, E> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Boolean
) = forAll<A, B, C, D, E>(
   computeDefaultIteration(genA, genB, genC, genD, genE),
   PropTestConfig(),
   genA,
   genB,
   genC,
   genD,
   genE,
   property
)

suspend fun <A, B, C, D, E> forAll(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Boolean
) = forAll<A, B, C, D, E>(
   computeDefaultIteration(genA, genB, genC, genD, genE),
   config,
   genA,
   genB,
   genC,
   genD,
   genE,
   property
)

suspend fun <A, B, C, D, E> forAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Boolean
) = forAll<A, B, C, D, E>(iterations, PropTestConfig(), genA, genB, genC, genD, genE, property)

suspend fun <A, B, C, D, E> forAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Boolean
) = proptest<A, B, C, D, E>(iterations, genA, genB, genC, genD, genE, config) { a, b, c, d, e ->
   property(
      a,
      b,
      c,
      d,
      e
   ) shouldBe true
}

suspend inline fun <reified A, reified B, reified C, reified D, reified E> forAll(
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
): PropertyContext = forAll<A, B, C, D, E>(PropertyTesting.defaultIterationCount, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
): PropertyContext = forAll(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
) = forAll(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
) = proptest(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   Arb.default<E>(),
   config
) { a, b, c, d, e -> property(a, b, c, d, e) shouldBe true }

suspend fun <A, B, C, D, E> forNone(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Boolean
) = forNone<A, B, C, D, E>(
   computeDefaultIteration(genA, genB, genC, genD, genE),
   config,
   genA,
   genB,
   genC,
   genD,
   genE,
   property
)

suspend fun <A, B, C, D, E> forNone(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Boolean
) = forNone<A, B, C, D, E>(iterations, PropTestConfig(), genA, genB, genC, genD, genE, property)

suspend fun <A, B, C, D, E> forNone(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Boolean
) = proptest<A, B, C, D, E>(iterations, genA, genB, genC, genD, genE, config) { a, b, c, d, e ->
   property(
      a,
      b,
      c,
      d,
      e
   ) shouldBe false
}

suspend inline fun <reified A, reified B, reified C, reified D, reified E> forNone(
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
): PropertyContext = forNone<A, B, C, D, E>(PropertyTesting.defaultIterationCount, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
): PropertyContext = forNone(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
) = forNone(iterations, PropTestConfig(), property)

suspend inline fun <reified A, reified B, reified C, reified D, reified E> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
) = proptest(
   iterations,
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   Arb.default<E>(),
   config
) { a, b, c, d, e -> property(a, b, c, d, e) shouldBe false }
