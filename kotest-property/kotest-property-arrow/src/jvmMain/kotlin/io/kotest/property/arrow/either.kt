package io.kotest.property.arrow

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.merge

/**
 * Generates approx 50/50 of left and right from the underlying generators.
 */
fun <A, B> Arb.Companion.either(arbLeft: Arb<A>, arbRight: Arb<B>): Arb<Either<A, B>> =
   left(arbLeft).merge(right(arbRight))

/**
 * Generates instances of [Either.Right] using the given arb.
 */
fun <B> Arb.Companion.right(arb: Arb<B>): Arb<Either<Nothing, B>> = arb.map { it.right() }

/**
 * Generates instances of [Either.Left] using the given arb.
 */
fun <A> Arb.Companion.left(arb: Arb<A>): Arb<Either<A, Nothing>> = arb.map { it.left() }
