package io.kotest.property.arrow.core

import arrow.core.Either
import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.map

public fun <A, B> Arb.Companion.either(left: Arb<A>, right: Arb<B>): Arb<Either<A, B>> =
  choice(left.map { Either.Left(it) }, right.map { Either.Right(it) })

public fun <A, B> Arb<A>.or(arbB: Arb<B>): Arb<Either<A, B>> =
  Arb.either(this, arbB)
