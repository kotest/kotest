package io.kotest.property.arrow.core

import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind

public fun <A, B, C, D> Arb.Companion.tuple4(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>
): Arb<Tuple4<A, B, C, D>> =
  Arb.bind(arbA, arbB, arbC, arbD, ::Tuple4)

public fun <A, B, C, D, E> Arb.Companion.tuple5(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>
): Arb<Tuple5<A, B, C, D, E>> =
  Arb.bind(arbA, arbB, arbC, arbD, arbE, ::Tuple5)

public fun <A, B, C, D, E, F> Arb.Companion.tuple6(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>,
  arbF: Arb<F>
): Arb<Tuple6<A, B, C, D, E, F>> =
  Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, ::Tuple6)

public fun <A, B, C, D, E, F, G> Arb.Companion.tuple7(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>,
  arbF: Arb<F>,
  arbG: Arb<G>
): Arb<Tuple7<A, B, C, D, E, F, G>> =
  Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, ::Tuple7)

public fun <A, B, C, D, E, F, G, H> Arb.Companion.tuple8(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>,
  arbF: Arb<F>,
  arbG: Arb<G>,
  arbH: Arb<H>
): Arb<Tuple8<A, B, C, D, E, F, G, H>> =
  Arb.bind(
    Arb.tuple7(arbA, arbB, arbC, arbD, arbE, arbF, arbG),
    arbH
  ) { (a, b, c, d, e, f, g), h ->
    Tuple8(a, b, c, d, e, f, g, h)
  }

public fun <A, B, C, D, E, F, G, H, I> Arb.Companion.tuple9(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>,
  arbF: Arb<F>,
  arbG: Arb<G>,
  arbH: Arb<H>,
  arbI: Arb<I>
): Arb<Tuple9<A, B, C, D, E, F, G, H, I>> =
  Arb.bind(
    Arb.tuple8(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH),
    arbI
  ) { (a, b, c, d, e, f, g, h), i ->
    Tuple9(a, b, c, d, e, f, g, h, i)
  }
