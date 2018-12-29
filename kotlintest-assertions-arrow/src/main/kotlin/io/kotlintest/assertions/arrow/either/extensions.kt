package io.kotlintest.assertions.arrow.either

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import io.kotlintest.properties.Gen
import io.kotlintest.properties.RANDOM

fun <A, B> choose(fa:() -> A, fb: () -> B): Either<A, B> =
  if (RANDOM.nextBoolean()) Left(fa()) else Right(fb())

fun <A, B> Gen.Companion.either(GA: Gen<A>, GB: Gen<B>): Gen<Either<A, B>> =
  object : Gen<Either<A, B>> {
    override fun constants(): Iterable<Either<A, B>> =
      GA.constants().map(::Left) + GB.constants().map(::Right)

    override fun random(): Sequence<Either<A, B>> =
      generateSequence {
        choose({ GA.random().iterator().next() }, { GB.random().iterator().next() })
      }
  }