@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.property.internal.test1

fun <A> checkAll(
   genA: Gen<A>,
   args: PropTestArgs = PropTestArgs(),
   property: PropertyContext.(A) -> Unit
): PropertyContext = test1(genA, args, property)

inline fun <reified A> checkAll(
   iterations: Int = 100,
   args: PropTestArgs = PropTestArgs(),
   noinline property: PropertyContext.(A) -> Unit
): PropertyContext = test1(
   Arbitrary.default(iterations),
   args,
   property
)

fun <A> forAll(
   genA: Gen<A>,
   args: PropTestArgs = PropTestArgs(),
   property: PropertyContext.(A) -> Boolean
) = test1(genA, args) { a -> property(a).shouldBeTrue() }

inline fun <reified A> forAll(
   iterations: Int = 100,
   args: PropTestArgs = PropTestArgs(),
   crossinline property: PropertyContext.(A) -> Boolean
) = test1<A>(
   Arbitrary.default(iterations),
   args
) { a -> property(a).shouldBeTrue() }
