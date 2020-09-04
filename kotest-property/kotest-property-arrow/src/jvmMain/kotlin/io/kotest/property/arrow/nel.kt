package io.kotest.property.arrow

import arrow.core.NonEmptyList
import io.kotest.property.Arb
import io.kotest.property.arbitrary.create
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.list

fun <A> Arb.Companion.nel(
   arbA: Arb<A>,
   range: IntRange = 1..100
): Arb<NonEmptyList<A>> {
   check(!range.isEmpty()) { "range must not be empty" }
   check(range.first >= 1) { "start of range must not be less than 1" }

   val edges = NonEmptyList.fromList(arbA.edgecases()).toList()

   return Arb.list(arbA, range).flatMap { list ->
      Arb.create(edges) { NonEmptyList.fromListUnsafe(list) }
   }
}

fun <A> Arb.Companion.nonEmptyList(arb: Arb<A>, range: IntRange = 1..100): Arb<NonEmptyList<A>> = nel(arb, range)
