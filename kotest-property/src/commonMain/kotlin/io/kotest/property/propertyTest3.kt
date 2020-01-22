@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.property.internal.test3

suspend fun <A, B, C> checkAll(
   genA: Argument<A>,
   genB: Argument<B>,
   genC: Argument<C>,
   config: PropTestConfig = PropTestConfig(),
   property: suspend PropertyContext.(A, B, C) -> Unit
): PropertyContext = test3(genA, genB, genC, config, property)

suspend inline fun <reified A, reified B, reified C> checkAll(
   iterations: Int = 100,
   config: PropTestConfig = PropTestConfig(),
   noinline property: suspend PropertyContext.(A, B, C) -> Unit
): PropertyContext = test3(
   Arbitrary.default(iterations),
   Arbitrary.default(iterations),
   Arbitrary.default(iterations),
   config,
   property
)

suspend fun <A, B, C> forAll(
   genA: Argument<A>,
   genB: Argument<B>,
   genC: Argument<C>,
   config: PropTestConfig = PropTestConfig(),
   property: suspend PropertyContext.(A, B, C) -> Boolean
) = test3(genA, genB, genC, config) { a, b, c ->
   property(
      a,
      b,
      c
   ).shouldBeTrue()
}

suspend inline fun <reified A, reified B, reified C> forAll(
   iterations: Int = 100,
   config: PropTestConfig = PropTestConfig(),
   crossinline property: suspend PropertyContext.(A, B, C) -> Boolean
) = test3<A, B, C>(
   Arbitrary.default(iterations),
   Arbitrary.default(iterations),
   Arbitrary.default(iterations),
   config
) { a, b, c -> property(a, b, c).shouldBeTrue() }
