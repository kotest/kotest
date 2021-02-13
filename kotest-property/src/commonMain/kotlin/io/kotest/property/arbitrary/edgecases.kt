package io.kotest.property.arbitrary

import io.kotest.fp.NonEmptyList
import io.kotest.property.EdgeCases
import io.kotest.property.RandomSource

fun <A, B, Z> EdgeCases.Companion.bind(
   ecA: EdgeCases<A>,
   ecB: EdgeCases<B>,
   fn: (A, B) -> Z
): EdgeCases<Z> = ecA.bind(ecB).map { (a, b) -> fn(a, b) }

private fun <A, B> EdgeCases<A>.bind(edgeB: EdgeCases<B>): EdgeCases<Pair<A, B>> = when (val edgeA = this) {
   is EdgeCases.Static -> when (edgeB) {
      is EdgeCases.Static -> EdgeCases.Static(
         edgeA.values.flatMap { a ->
            edgeB.values.map { b -> a to b }
         }
      )
      is EdgeCases.Random -> EdgeCases.Random { rs ->
         edgeA.values.flatMap { a ->
            edgeB.generate(rs).map { b ->
               a to b
            }
         }
      }
   }
   is EdgeCases.Random -> when (edgeB) {
      is EdgeCases.Static -> EdgeCases.Random { rs ->
         edgeB.values.flatMap { b ->
            edgeA.generate(rs).map { a ->
               a to b
            }
         }
      }
      is EdgeCases.Random -> EdgeCases.Random { rs ->
         edgeA.generate(rs).flatMap { a ->
            edgeB.generate(rs).map { b ->
               a to b
            }
         }
      }
   }
}

fun <A, B> EdgeCases<A>.map(fn: (A) -> B): EdgeCases<B> = when (this) {
   is EdgeCases.Static<A> -> EdgeCases.Static(this.values.map(fn))
   is EdgeCases.Random<A> -> EdgeCases.Random { rs -> this.generate(rs).map(fn) }
}

fun <A, B> EdgeCases<A>.flatMap(fn: (A) -> EdgeCases<B>): EdgeCases<B> = when (this) {
   is EdgeCases.Static -> this.values.map(fn).reduce { first, next -> first + next }
   is EdgeCases.Random -> EdgeCases.Random { rs ->
      val edgesB: NonEmptyList<EdgeCases<B>> = this.generate(rs).map(fn)
      edgesB.flatMap { edgeB ->
         when (edgeB) {
            is EdgeCases.Static -> edgeB.values
            is EdgeCases.Random -> edgeB.generate(rs)
         }
      }
   }
}

fun <A> EdgeCases<A>.merge(other: EdgeCases<A>): EdgeCases<A> = this + other

fun <A> edgecases(first: A, vararg rest: A): EdgeCases<A> = EdgeCases.of(first, *rest)

fun <A> edgecases(generate: (RandomSource) -> A): EdgeCases<A> = EdgeCases.random(generate)
