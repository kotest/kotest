package io.kotest.property.arrow.core

import io.kotest.property.Arb
import io.kotest.property.arbitrary.map

public fun <A, B> Arb.Companion.functionAToB(arb: Arb<B>): Arb<(A) -> B> =
  arb.map { b: B -> { _: A -> b } }

public fun <A> Arb.Companion.functionAAToA(arb: Arb<A>): Arb<(A, A) -> A> =
  arb.map { a: A -> { _: A, _: A -> a } }

public fun <A, B> Arb.Companion.functionBAToB(arb: Arb<B>): Arb<(B, A) -> B> =
  arb.map { b: B -> { _: B, _: A -> b } }

public fun <A, B> Arb.Companion.functionABToB(arb: Arb<B>): Arb<(A, B) -> B> =
  arb.map { b: B -> { _: A, _: B -> b } }

public fun <A> Arb.Companion.functionToA(arb: Arb<A>): Arb<() -> A> =
  arb.map { a: A -> { a } }
