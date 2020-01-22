@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.property.internal.test1

suspend fun <A> checkAll(
   genA: Argument<A>,
   config: PropTestConfig = PropTestConfig(),
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext = test1(genA, config, property)

suspend inline fun <reified A> checkAll(
   iterations: Int = 100,
   config: PropTestConfig = PropTestConfig(),
   noinline property: suspend PropertyContext.(A) -> Unit
): PropertyContext = test1(
   Arbitrary.default(iterations),
   config,
   property
)

suspend fun <A> forAll(
   genA: Argument<A>,
   config: PropTestConfig = PropTestConfig(),
   property: PropertyContext.(A) -> Boolean
) = test1(genA, config) { a -> property(a).shouldBeTrue() }

suspend inline fun <reified A> forAll(
   iterations: Int = 100,
   config: PropTestConfig = PropTestConfig(),
   crossinline property: suspend PropertyContext.(A) -> Boolean
) = test1<A>(
   Arbitrary.default(iterations),
   config
) { a -> property(a).shouldBeTrue() }
