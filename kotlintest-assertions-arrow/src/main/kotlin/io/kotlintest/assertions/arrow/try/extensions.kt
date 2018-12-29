package io.kotlintest.assertions.arrow.`try`

import arrow.core.*
import io.kotlintest.properties.Gen
import io.kotlintest.properties.RANDOM
import java.lang.RuntimeException

object Ex : RuntimeException("BOOM")

fun <A> choose(fa:() -> A): Try<A> =
  if (RANDOM.nextBoolean()) Failure(Ex) else Success(fa())

fun <A> Gen.Companion.`try`(GA: Gen<A>): Gen<Try<A>> =
  object : Gen<Try<A>> {
    override fun constants(): Iterable<Try<A>> =
      GA.constants().map(::Success)

    override fun random(): Sequence<Try<A>> =
      generateSequence {
        choose { GA.random().iterator().next() }
      }
  }