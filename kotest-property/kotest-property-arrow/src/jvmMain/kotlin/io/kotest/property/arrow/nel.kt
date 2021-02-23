package io.kotest.property.arrow

import arrow.core.NonEmptyList
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.withEdgecases

fun <A> Arb.Companion.nel(
   arbA: Arb<A>,
   range: IntRange = 1..100
): Arb<NonEmptyList<A>> {
   check(!range.isEmpty()) { "range must not be empty" }
   check(range.first >= 1) { "start of range must not be less than 1" }

   return Arb
      .list(arbA, range)
      .map { NonEmptyList.fromListUnsafe(it) }
}

fun <A> Arb.Companion.nonEmptyList(arb: Arb<A>, range: IntRange = 1..100): Arb<NonEmptyList<A>> = nel(arb, range)
