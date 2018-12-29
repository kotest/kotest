package io.kotlintest.assertions.arrow.option

import arrow.core.*
import io.kotlintest.properties.Gen
import io.kotlintest.properties.RANDOM

fun <A> choose(fa:() -> A): Option<A> =
  if (RANDOM.nextBoolean()) None else Some(fa())

fun <A> Gen.Companion.option(GA: Gen<A>): Gen<Option<A>> =
  object : Gen<Option<A>> {
    override fun constants(): Iterable<Option<A>> =
      GA.constants().map(::Some)

    override fun random(): Sequence<Option<A>> =
      generateSequence {
        choose { GA.random().iterator().next() }
      }
  }