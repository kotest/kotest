@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.property.internal.test2

suspend fun <A, B> checkAll(
   genA: Argument<A>,
   genB: Argument<B>,
   config: PropTestConfig = PropTestConfig(),
   property: suspend PropertyContext.(A, B) -> Unit
): PropertyContext = test2(genA, genB, config, property)

suspend inline fun <reified A, reified B> checkAll(
   iterations: Int = 100,
   config: PropTestConfig = PropTestConfig(),
   noinline property: suspend PropertyContext.(A, B) -> Unit
) = test2<A, B>(
   Arbitrary.default(iterations),
   Arbitrary.default(iterations),
   config,
   property
)

suspend fun <A, B> forAll(
   genA: Argument<A>,
   genB: Argument<B>,
   config: PropTestConfig = PropTestConfig(),
   property: suspend PropertyContext.(A, B) -> Boolean
) = test2(genA, genB, config) { a, b -> property(a, b).shouldBeTrue() }

suspend inline fun <reified A, reified B> forAll(
   iterations: Int = 100,
   config: PropTestConfig = PropTestConfig(),
   noinline property: suspend PropertyContext.(A, B) -> Boolean
) = test2<A, B>(
   Arbitrary.default(iterations),
   Arbitrary.default(iterations),
   config
) { a, b -> property(a, b).shouldBeTrue() }
