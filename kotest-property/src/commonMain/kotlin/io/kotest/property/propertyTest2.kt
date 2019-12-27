@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.property.internal.test2

inline fun <A, B> checkAll(
   genA: Gen<A>,
   genB: Gen<B>,
   args: PropTestArgs = PropTestArgs(),
   property: PropertyContext.(A, B) -> Unit
): PropertyContext = test2(genA, genB, args, property)

inline fun <reified A, reified B> checkAll(
   iterations: Int = 100,
   args: PropTestArgs = PropTestArgs(),
   property: PropertyContext.(A, B) -> Unit
): PropertyContext = test2(
   Arbitrary.default(iterations),
   Arbitrary.default(iterations),
   args,
   property
)

inline fun <A, B> forAll(
   genA: Gen<A>,
   genB: Gen<B>,
   args: PropTestArgs = PropTestArgs(),
   property: PropertyContext.(A, B) -> Boolean
) = test2(genA, genB, args) { a, b -> property(a, b).shouldBeTrue() }

inline fun <reified A, reified B> forAll(
   iterations: Int = 100,
   args: PropTestArgs = PropTestArgs(),
   property: PropertyContext.(A, B) -> Boolean
) = test2<A, B>(
   Arbitrary.default(iterations),
   Arbitrary.default(iterations),
   args
) { a, b -> property(a, b).shouldBeTrue() }
