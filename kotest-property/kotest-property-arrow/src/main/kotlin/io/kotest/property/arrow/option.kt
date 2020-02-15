package io.kotest.property.arrow

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.none
import arrow.core.some
import io.kotest.property.Gen
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.arb
import io.kotest.property.exhaustive.Exhaustive
import io.kotest.property.exhaustive.exhaustive

fun <A> Exhaustive.Companion.option(a: A) = exhaustive(listOf(None, Some(a)))

fun <A> Arb.Companion.some(gen: Gen<A>): Arb<Option<A>> = arb {
   gen.generate(it).single().value.some()
}

fun <A> Arb.Companion.option(gen: Gen<A>): Arb<Option<A>> = arb {
   when (it.random.nextBoolean()) {
      true -> none()
      false -> gen.generate(it).single().value.some()
   }
}
