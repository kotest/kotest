package io.kotest.property

import io.kotest.fp.NonEmptyList
import io.kotest.fp.nel

sealed class EdgeCases<out A> {
   data class Static<A>(val values: NonEmptyList<A>) : EdgeCases<A>()
   data class Random<A>(val generate: (RandomSource) -> NonEmptyList<A>) : EdgeCases<A>()

   companion object {
      fun <A> random(generate: (RandomSource) -> A): EdgeCases<A> = Random { generate(it).nel() }
      fun <A> of(first: A, vararg rest: A): EdgeCases<A> = Static(NonEmptyList(first, rest.toList()))
   }

   operator fun plus(other: EdgeCases<@UnsafeVariance A>): EdgeCases<A> = when (this) {
      is Static -> when (other) {
         is Static -> Static(this.values + other.values)
         is Random -> Random { rs -> this.values + other.generate(rs) }
      }
      is Random -> when (other) {
         is Static -> Random { rs -> this.generate(rs) + other.values }
         is Random -> Random { rs -> this.generate(rs) + other.generate(rs) }
      }
   }

   fun values(rs: RandomSource): List<A> = when (this) {
      is Static -> this.values.all
      is Random -> this.generate(rs).all
   }
}
