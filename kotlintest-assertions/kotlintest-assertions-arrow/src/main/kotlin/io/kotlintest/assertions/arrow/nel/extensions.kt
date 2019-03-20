package io.kotlintest.assertions.arrow.nel

import arrow.data.NonEmptyList
import io.kotlintest.properties.Gen

/**
 * [Gen] extension instance for [NonEmptyList].
 *
 * Generates random [NonEmptyList] of [A] as provided by the [GA] generators.
 * Includes a constant [head] value to ensure all lists are non-empty.
 *
 * ```kotlin
 * import io.kotlintest.assertions.arrow.nel.nel
 * import io.kotlintest.properties.forAll
 * import io.kotlintest.properties.Gen
 *
 * forAll(Gen.nel(Gen.int(), 0)) { it.contains(0) }
 * ```
 */
inline fun <reified A> Gen.Companion.nel(GA: Gen<A>, head: A): Gen<NonEmptyList<A>> =
  object : Gen<NonEmptyList<A>> {
    override fun constants(): Iterable<NonEmptyList<A>> =
      listOf(NonEmptyList(head, GA.constants().toList()))

    override fun random(): Sequence<NonEmptyList<A>> =
      generateSequence {
        val size = Gen.int().random().iterator().next()
        val tail = GA.random().take(size)
        NonEmptyList.of(head, *tail.toList().toTypedArray())
      }
  }