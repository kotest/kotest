package io.kotest.property.arrow

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.kotest.property.Gen
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.arb

/**
 * Generates approx 50/50 of left and right from the underlying generators.
 */
fun <A, B> Arb.Companion.either(left: Gen<A>, right: Gen<B>): Arb<Either<A, B>> = arb {
   when (it.random.nextBoolean()) {
      true -> left.generate(it).single().value.left()
      false -> right.generate(it).single().value.right()
   }
}

/**
 * Generates instances of [Right] using the given generator.
 */
fun <B> Arb.Companion.right(gen: Gen<B>): Arb<Either<Nothing, B>> = arb { gen.generate(it).single().value.right() }

/**
 * Generates instances of [Left] using the given generator.
 */
fun <A> Arb.Companion.left(gen: Gen<A>): Arb<Either<A, Nothing>> = arb { gen.generate(it).single().value.left() }
