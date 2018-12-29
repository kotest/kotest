package io.kotlintest.assertions.arrow.nel

import arrow.data.NonEmptyList
import io.kotlintest.properties.Gen
import io.kotlintest.properties.RANDOM

inline fun <reified A> Gen.Companion.nel(GA: Gen<A>, head: A): Gen<NonEmptyList<A>> =
  object : Gen<NonEmptyList<A>> {
    override fun constants(): Iterable<NonEmptyList<A>> =
      listOf(NonEmptyList(head, GA.constants().toList()))

    override fun random(): Sequence<NonEmptyList<A>> =
      generateSequence {
        val size = RANDOM.nextInt(100)
        val tail = GA.random().take(size)
        NonEmptyList.of(head, *tail.toList().toTypedArray())
      }
  }