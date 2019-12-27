@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.property.internal.test3

inline fun <A, B, C> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   args: PropTestArgs = PropTestArgs(),
   property: PropertyContext.(A, B, C) -> Unit
): PropertyContext = test3(genA, genB, genC, args, property)

inline fun <reified A, reified B, reified C> checkAll(
   iterations: Int = 100,
   args: PropTestArgs = PropTestArgs(),
   property: PropertyContext.(A, B, C) -> Unit
): PropertyContext = test3(
   Arbitrary.default(iterations),
   Arbitrary.default(iterations),
   Arbitrary.default(iterations),
   args,
   property
)

inline fun <A, B, C> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   args: PropTestArgs = PropTestArgs(),
   property: PropertyContext.(A, B, C) -> Boolean
) = test3(genA, genB, genC, args) { a, b, c ->
   property(
      a,
      b,
      c
   ).shouldBeTrue()
}

inline fun <reified A, reified B, reified C> forAll(
   iterations: Int = 100,
   args: PropTestArgs = PropTestArgs(),
   property: PropertyContext.(A, B, C) -> Boolean
) = test3<A, B, C>(
   Arbitrary.default(iterations),
   Arbitrary.default(iterations),
   Arbitrary.default(iterations),
   args
) { a, b, c -> property(a, b, c).shouldBeTrue() }
