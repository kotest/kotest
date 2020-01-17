@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.property.internal.test1

suspend fun <A> checkAll(
   genA: Gen<A>,
   args: PropTestArgs = PropTestArgs(),
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext = test1(genA, args, property)

suspend inline fun <reified A> checkAll(
   iterations: Int = 100,
   args: PropTestArgs = PropTestArgs(),
   noinline property: suspend PropertyContext.(A) -> Unit
): PropertyContext = test1(
   Arbitrary.default(iterations),
   args,
   property
)

suspend fun <A> forAll(
   genA: Gen<A>,
   args: PropTestArgs = PropTestArgs(),
   property: PropertyContext.(A) -> Boolean
) = test1(genA, args) { a -> property(a).shouldBeTrue() }

suspend inline fun <reified A> forAll(
   iterations: Int = 100,
   args: PropTestArgs = PropTestArgs(),
   crossinline property: suspend PropertyContext.(A) -> Boolean
) = test1<A>(
   Arbitrary.default(iterations),
   args
) { a -> property(a).shouldBeTrue() }
