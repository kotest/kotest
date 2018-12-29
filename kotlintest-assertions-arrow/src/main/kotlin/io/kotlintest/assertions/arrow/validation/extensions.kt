package io.kotlintest.assertions.arrow.validation

import arrow.core.Left
import arrow.core.Right
import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.Validated
import io.kotlintest.properties.Gen
import io.kotlintest.properties.RANDOM

fun <A, B> choose(fa:() -> A, fb: () -> B): Validated<A, B> =
  if (RANDOM.nextBoolean()) Invalid(fa()) else Valid(fb())

fun <A, B> Gen.Companion.validated(GA: Gen<A>, GB: Gen<B>): Gen<Validated<A, B>> =
  object : Gen<Validated<A, B>> {
    override fun constants(): Iterable<Validated<A, B>> =
      GA.constants().map(::Invalid) + GB.constants().map(::Valid)

    override fun random(): Sequence<Validated<A, B>> =
      generateSequence {
        choose({ GA.random().iterator().next() }, { GB.random().iterator().next() })
      }
  }