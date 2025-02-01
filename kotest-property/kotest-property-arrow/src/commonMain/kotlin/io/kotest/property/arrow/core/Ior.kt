package io.kotest.property.arrow.core

import arrow.core.Ior
import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.bind


public fun <A, B> Arb.Companion.ior(left: Arb<A>, right: Arb<B>): Arb<Ior<A, B>> =
  Arb.choice(left.map { Ior.Left(it) },
             Arb.bind(left, right) { a, b -> Ior.Both(a, b) },
             right.map { Ior.Right(it) })

public fun <A, B> Arb<A>.alignWith(arbB: Arb<B>): Arb<Ior<A, B>> =
  Arb.ior(this, arbB)
