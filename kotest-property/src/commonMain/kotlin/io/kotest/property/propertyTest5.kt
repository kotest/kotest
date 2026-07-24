@file:OptIn(ExperimentalKotest::class)

package io.kotest.property

import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.shouldBe
import io.kotest.property.internal.proptest
import io.kotest.property.resolution.default

@IgnorableReturnValue
suspend fun <A, B, C, D, E> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Unit
): PropertyContext = proptest(
   genA,
   genB,
   genC,
   genD,
   genE,
   PropTestConfig(),
   property
)

@IgnorableReturnValue
suspend fun <A, B, C, D, E> checkAll(
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Unit
): PropertyContext = proptest(
   genA,
   genB,
   genC,
   genD,
   genE,
   config,
   property
)

@IgnorableReturnValue
suspend fun <A, B, C, D, E> checkAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Unit
): PropertyContext = proptest(genA, genB, genC, genD, genE, PropTestConfig(constraints = Constraints.iterations(iterations)), property)

@IgnorableReturnValue
suspend fun <A, B, C, D, E> checkAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Unit
): PropertyContext = proptest(genA, genB, genC, genD, genE, config.copy(iterations = iterations), property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E> checkAll(
   noinline property: suspend PropertyContext.(A, B, C, D, E) -> Unit
) = proptest(
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   PropTestConfig(),
   property
)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E) -> Unit
) = proptest(
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   config,
   property
)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A, B, C, D, E) -> Unit
) = proptest(
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   Arb.default(),
   PropTestConfig(constraints = Constraints.iterations(iterations)),
   property
)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A, B, C, D, E) -> Unit
) = proptest<A, B, C, D, E>(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   Arb.default<E>(),
   config.copy(iterations = iterations),
   property
)

@IgnorableReturnValue
suspend fun <A, B, C, D, E> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Boolean
) = forAll<A, B, C, D, E>(
   PropTestConfig(),
   genA,
   genB,
   genC,
   genD,
   genE,
   property
)

@IgnorableReturnValue
suspend fun <A, B, C, D, E> forAll(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Boolean
) = proptest<A, B, C, D, E>(genA, genB, genC, genD, genE, config) { a, b, c, d, e ->
   property(
      a,
      b,
      c,
      d,
      e
   ) shouldBe true
}

@IgnorableReturnValue
suspend fun <A, B, C, D, E> forAll(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Boolean
) = forAll<A, B, C, D, E>(PropTestConfig(constraints = Constraints.iterations(iterations)), genA, genB, genC, genD, genE, property)

@IgnorableReturnValue
suspend fun <A, B, C, D, E> forAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Boolean
) = forAll<A, B, C, D, E>(config.copy(iterations = iterations), genA, genB, genC, genD, genE, property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E> forAll(
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
): PropertyContext = forAll<A, B, C, D, E>(PropTestConfig(), property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E> forAll(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
): PropertyContext = proptest(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   Arb.default<E>(),
   config
) { a, b, c, d, e -> property(a, b, c, d, e) shouldBe true }

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E> forAll(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
) = forAll(PropTestConfig(constraints = Constraints.iterations(iterations)), property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
) = proptest(
   Arb.default<A>(),
   Arb.default<B>(),
   Arb.default<C>(),
   Arb.default<D>(),
   Arb.default<E>(),
   config.copy(iterations = iterations),
) { a, b, c, d, e -> property(a, b, c, d, e) shouldBe true }

@IgnorableReturnValue
suspend fun <A, B, C, D, E> forNone(
   config: PropTestConfig = PropTestConfig(),
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Boolean
) = proptest<A, B, C, D, E>(genA, genB, genC, genD, genE, config) { a, b, c, d, e ->
   property(
      a,
      b,
      c,
      d,
      e
   ) shouldBe false
}

@IgnorableReturnValue
suspend fun <A, B, C, D, E> forNone(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Boolean
): PropertyContext = forNone<A, B, C, D, E>(PropTestConfig(constraints = Constraints.iterations(iterations)), genA, genB, genC, genD, genE, property)

@IgnorableReturnValue
suspend fun <A, B, C, D, E> forNone(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Boolean
) = forNone<A, B, C, D, E>(config.copy(iterations = iterations), genA, genB, genC, genD, genE, property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E> forNone(
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
): PropertyContext = forNone<A, B, C, D, E>(PropTestConfig(), property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E> forNone(
   config: PropTestConfig = PropTestConfig(),
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
): PropertyContext =
   proptest(
      Arb.default<A>(),
      Arb.default<B>(),
      Arb.default<C>(),
      Arb.default<D>(),
      Arb.default<E>(),
      config
   ) { a, b, c, d, e -> property(a, b, c, d, e) shouldBe false }

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E> forNone(
   iterations: Int,
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
): PropertyContext = forNone(PropTestConfig(constraints = Constraints.iterations(iterations)), property)

@IgnorableReturnValue
suspend inline fun <reified A, reified B, reified C, reified D, reified E> forNone(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: PropertyContext.(A, B, C, D, E) -> Boolean
) = forNone(config.copy(iterations = iterations), property)
