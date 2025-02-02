package io.kotest.property.arrow.core

import arrow.core.NonEmptyList
import arrow.core.toNonEmptyListOrNull
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map

public fun <A> Arb.Companion.nel(a: Arb<A>): Arb<NonEmptyList<A>> = nonEmptyList(a)

public fun <A> Arb.Companion.nonEmptyList(a: Arb<A>): Arb<NonEmptyList<A>> =
  list(a)
    .filter(List<A>::isNotEmpty)
    .map { it.toNonEmptyListOrNull()!! }

public fun <A> Arb.Companion.nel(a: Arb<A>, size: IntRange): Arb<NonEmptyList<A>> = nonEmptyList(a, size)

public fun <A> Arb.Companion.nonEmptyList(a: Arb<A>, size: IntRange): Arb<NonEmptyList<A>> =
  list(a, size)
    .filter(List<A>::isNotEmpty)
    .map { it.toNonEmptyListOrNull()!! }
