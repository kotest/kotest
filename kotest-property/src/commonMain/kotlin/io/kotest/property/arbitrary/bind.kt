package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.EdgeCases
import io.kotest.property.Exhaustive
import io.kotest.property.Gen

fun <A, B, T> Arb.Companion.bind(genA: Gen<A>, genB: Gen<B>, bindFn: (A, B) -> T): Arb<T> =
   genA.bind(genB, bindFn)

fun <A, B, C, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   bindFn: (A, B, C) -> T
): Arb<T> = genA.bind(genB).bind(genC).map { (ab, c) ->
   val (a, b) = ab
   bindFn(a, b, c)
}

fun <A, B, C, D, T> Arb.Companion.bind(
   genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>,
   bindFn: (A, B, C, D) -> T
): Arb<T> = genA.bind(genB).bind(genC).bind(genD).map { (abc, d) ->
   val (ab, c) = abc
   val (a, b) = ab
   bindFn(a, b, c, d)
}

fun <A, B, C, D, E, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   bindFn: (A, B, C, D, E) -> T
): Arb<T> = genA.bind(genB).bind(genC).bind(genD).bind(genE).map { (abcd, e) ->
   val (abc, d) = abcd
   val (ab, c) = abc
   val (a, b) = ab
   bindFn(a, b, c, d, e)
}

fun <A, B, C, D, E, F, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   bindFn: (A, B, C, D, E, F) -> T
): Arb<T> = genA.bind(genB).bind(genC).bind(genD).bind(genE).bind(genF).map { (abcde, f) ->
   val (abcd, e) = abcde
   val (abc, d) = abcd
   val (ab, c) = abc
   val (a, b) = ab
   bindFn(a, b, c, d, e, f)
}

fun <A, B, C, D, E, F, G, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   bindFn: (A, B, C, D, E, F, G) -> T
): Arb<T> = genA.bind(genB).bind(genC).bind(genD).bind(genE).bind(genF).bind(genG).map { (abcdef, g) ->
   val (abcde, f) = abcdef
   val (abcd, e) = abcde
   val (abc, d) = abcd
   val (ab, c) = abc
   val (a, b) = ab
   bindFn(a, b, c, d, e, f, g)
}

fun <A, B, C, D, E, F, G, H, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   bindFn: (A, B, C, D, E, F, G, H) -> T
): Arb<T> = genA.bind(genB).bind(genC).bind(genD).bind(genE).bind(genF).bind(genG).bind(genH).map { (abcdefg, h) ->
   val (abcdef, g) = abcdefg
   val (abcde, f) = abcdef
   val (abcd, e) = abcde
   val (abc, d) = abcd
   val (ab, c) = abc
   val (a, b) = ab
   bindFn(a, b, c, d, e, f, g, h)
}

fun <A, B, C, D, E, F, G, H, I, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   bindFn: (A, B, C, D, E, F, G, H, I) -> T
): Arb<T> = genA.bind(genB).bind(genC).bind(genD).bind(genE).bind(genF).bind(genG).bind(genH).bind(genI)
   .map { (abcdefgh, i) ->
      val (abcdefg, h) = abcdefgh
      val (abcdef, g) = abcdefg
      val (abcde, f) = abcdef
      val (abcd, e) = abcde
      val (abc, d) = abcd
      val (ab, c) = abc
      val (a, b) = ab
      bindFn(a, b, c, d, e, f, g, h, i)
   }

fun <A, B, C, D, E, F, G, H, I, J, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   genJ: Gen<J>,
   bindFn: (A, B, C, D, E, F, G, H, I, J) -> T
): Arb<T> = genA.bind(genB).bind(genC).bind(genD).bind(genE).bind(genF).bind(genG).bind(genH).bind(genI).bind(genJ)
   .map { (abcdefghi, j) ->
      val (abcdefgh, i) = abcdefghi
      val (abcdefg, h) = abcdefgh
      val (abcdef, g) = abcdefg
      val (abcde, f) = abcdef
      val (abcd, e) = abcde
      val (abc, d) = abcd
      val (ab, c) = abc
      val (a, b) = ab
      bindFn(a, b, c, d, e, f, g, h, i, j)
   }

fun <A, B, C, D, E, F, G, H, I, J, K, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   genJ: Gen<J>,
   genK: Gen<K>,
   bindFn: (A, B, C, D, E, F, G, H, I, J, K) -> T
): Arb<T> =
   genA.bind(genB).bind(genC).bind(genD).bind(genE).bind(genF).bind(genG).bind(genH).bind(genI).bind(genJ).bind(genK)
      .map { (abcdefghij, k) ->
         val (abcdefghi, j) = abcdefghij
         val (abcdefgh, i) = abcdefghi
         val (abcdefg, h) = abcdefgh
         val (abcdef, g) = abcdefg
         val (abcde, f) = abcdef
         val (abcd, e) = abcde
         val (abc, d) = abcd
         val (ab, c) = abc
         val (a, b) = ab
         bindFn(a, b, c, d, e, f, g, h, i, j, k)
      }

fun <A, B, C, D, E, F, G, H, I, J, K, L, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   genJ: Gen<J>,
   genK: Gen<K>,
   genL: Gen<L>,
   bindFn: (A, B, C, D, E, F, G, H, I, J, K, L) -> T
): Arb<T> =
   genA.bind(genB).bind(genC).bind(genD).bind(genE).bind(genF).bind(genG).bind(genH).bind(genI).bind(genJ).bind(genK)
      .bind(genL)
      .map { (abcdefghijk, l) ->
         val (abcdefghij, k) = abcdefghijk
         val (abcdefghi, j) = abcdefghij
         val (abcdefgh, i) = abcdefghi
         val (abcdefg, h) = abcdefgh
         val (abcdef, g) = abcdefg
         val (abcde, f) = abcdef
         val (abcd, e) = abcde
         val (abc, d) = abcd
         val (ab, c) = abc
         val (a, b) = ab
         bindFn(a, b, c, d, e, f, g, h, i, j, k, l)
      }

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   genJ: Gen<J>,
   genK: Gen<K>,
   genL: Gen<L>,
   genM: Gen<M>,
   bindFn: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> T
): Arb<T> =
   genA.bind(genB).bind(genC).bind(genD).bind(genE).bind(genF).bind(genG).bind(genH).bind(genI).bind(genJ).bind(genK)
      .bind(genL).bind(genM)
      .map { (abcdefghijkl, m) ->
         val (abcdefghijk, l) = abcdefghijkl
         val (abcdefghij, k) = abcdefghijk
         val (abcdefghi, j) = abcdefghij
         val (abcdefgh, i) = abcdefghi
         val (abcdefg, h) = abcdefgh
         val (abcdef, g) = abcdefg
         val (abcde, f) = abcdef
         val (abcd, e) = abcde
         val (abc, d) = abcd
         val (ab, c) = abc
         val (a, b) = ab
         bindFn(a, b, c, d, e, f, g, h, i, j, k, l, m)
      }

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   genJ: Gen<J>,
   genK: Gen<K>,
   genL: Gen<L>,
   genM: Gen<M>,
   genN: Gen<N>,
   bindFn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> T
): Arb<T> =
   genA.bind(genB).bind(genC).bind(genD).bind(genE).bind(genF).bind(genG).bind(genH).bind(genI).bind(genJ).bind(genK)
      .bind(genL).bind(genM).bind(genN)
      .map { (abcdefghijklm, n) ->
         val (abcdefghijkl, m) = abcdefghijklm
         val (abcdefghijk, l) = abcdefghijkl
         val (abcdefghij, k) = abcdefghijk
         val (abcdefghi, j) = abcdefghij
         val (abcdefgh, i) = abcdefghi
         val (abcdefg, h) = abcdefgh
         val (abcdef, g) = abcdefg
         val (abcde, f) = abcdef
         val (abcd, e) = abcde
         val (abc, d) = abcd
         val (ab, c) = abc
         val (a, b) = ab
         bindFn(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
      }

private fun <A, B, C> Gen<A>.bind(other: Gen<B>, fn: (A, B) -> C): Arb<C> {
   val arb = when (this) {
      is Arb -> this
      is Exhaustive -> this.toArb()
   }

   val otherArb = when (other) {
      is Arb -> other
      is Exhaustive -> other.toArb()
   }

   return arb
      .flatMap { a -> otherArb.map { b -> fn(a, b) } }
      .withEdges(EdgeCases.bind(arb.edges(), otherArb.edges(), fn))
}

private fun <A, B> Gen<A>.bind(other: Gen<B>): Arb<Pair<A, B>> = this.bind(other, ::Pair)
