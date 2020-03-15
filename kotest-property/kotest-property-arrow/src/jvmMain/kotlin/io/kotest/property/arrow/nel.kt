package io.kotest.property.arrow

import arrow.core.NonEmptyList
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import kotlin.random.nextInt

inline fun <reified A> Arb.Companion.nel(
   arbA: Arb<A>,
   range: IntRange = 1..100
): Arb<NonEmptyList<A>> = object : Arb<NonEmptyList<A>>() {
   override fun edgecases(): List<NonEmptyList<A>> {
      val edges = arbA.edgecases().toList()
      return if (edges.isEmpty()) emptyList() else {
         val head = edges.first()
         val tail = edges.drop(1)
         listOf(NonEmptyList.of(head, *tail.toList().toTypedArray()))
      }
   }

   override fun values(rs: RandomSource): Sequence<Sample<NonEmptyList<A>>> = generateSequence {
      val size = rs.random.nextInt(range)
      val `as` = arbA.values(rs).take(size).map { it.value }
      val head = `as`.first()
      val tail = `as`.drop(1)
      Sample(NonEmptyList.of(head, *tail.toList().toTypedArray()))
   }
}
