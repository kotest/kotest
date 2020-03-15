package io.kotest.property.arrow

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arb

/**
 * Generates approx 50/50 of left and right from the underlying generators.
 */
fun <A, B> Arb.Companion.either(left: Arb<A>, right: Arb<B>): Arb<Either<A, B>> = arb { rs ->
   val aa = left.values(rs)
   val bb = right.values(rs)
   generateSequence {
      when (rs.random.nextBoolean()) {
         true -> aa.first().value.left()
         false -> bb.first().value.right()
      }
   }
}

/**
 * Generates instances of [Either.Right] using the given arb.
 */
fun <B> Arb.Companion.right(arb: Arb<B>): Arb<Either<Nothing, B>> = arb { rs ->
   arb.values(rs).map { Either.Right(it.value) }
}

/**
 * Generates instances of [Either.Left] using the given arb.
 */
fun <A> Arb.Companion.left(arb: Arb<A>): Arb<Either<A, Nothing>> = arb { rs ->
   arb.values(rs).map { Either.Left(it.value) }
}
