package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.RTree
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.asSample

fun <A, B, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   bindFn: (A, B) -> T
): Arb<T> =
   Arb.bindN(
      genA,
      genB,
      Arb.unit(),
      Arb.unit(),
      Arb.unit(),
      Arb.unit(),
      Arb.unit(),
      Arb.unit(),
      Arb.unit(),
      Arb.unit(),
      Arb.unit(),
      Arb.unit(),
      Arb.unit(),
      Arb.unit()
   ) { a, b, _, _, _, _, _, _, _, _, _, _, _, _ ->
      bindFn(a, b)
   }

fun <A, B, C, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   bindFn: (A, B, C) -> T
): Arb<T> = Arb.bindN(
   genA,
   genB,
   genC,
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit()
) { a, b, c, _, _, _, _, _, _, _, _, _, _, _ ->
   bindFn(a, b, c)
}

fun <A, B, C, D, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   bindFn: (A, B, C, D) -> T
): Arb<T> = Arb.bindN(
   genA,
   genB,
   genC,
   genD,
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit()
) { a, b, c, d, _, _, _, _, _, _, _, _, _, _ ->
   bindFn(a, b, c, d)
}

fun <A, B, C, D, E, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   bindFn: (A, B, C, D, E) -> T
): Arb<T> = Arb.bindN(
   genA,
   genB,
   genC,
   genD,
   genE,
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit()
) { a, b, c, d, e, _, _, _, _, _, _, _, _, _ ->
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
): Arb<T> = Arb.bindN(
   genA,
   genB,
   genC,
   genD,
   genE,
   genF,
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit()
) { a, b, c, d, e, f, _, _, _, _, _, _, _, _ ->
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
): Arb<T> = Arb.bindN(
   genA,
   genB,
   genC,
   genD,
   genE,
   genF,
   genG,
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit()
) { a, b, c, d, e, f, g, _, _, _, _, _, _, _ ->
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
): Arb<T> = Arb.bindN(
   genA,
   genB,
   genC,
   genD,
   genE,
   genF,
   genG,
   genH,
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit()
) { a, b, c, d, e, f, g, h, _, _, _, _, _, _ ->
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
): Arb<T> = Arb.bindN(
   genA,
   genB,
   genC,
   genD,
   genE,
   genF,
   genG,
   genH,
   genI,
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit()
) { a, b, c, d, e, f, g, h, i, _, _, _, _, _ ->
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
): Arb<T> = Arb.bindN(
   genA,
   genB,
   genC,
   genD,
   genE,
   genF,
   genG,
   genH,
   genI,
   genJ,
   Arb.unit(),
   Arb.unit(),
   Arb.unit(),
   Arb.unit()
) { a, b, c, d, e, f, g, h, i, j, _, _, _, _ ->
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
): Arb<T> = Arb.bindN(
   genA,
   genB,
   genC,
   genD,
   genE,
   genF,
   genG,
   genH,
   genI,
   genJ,
   genK,
   Arb.unit(),
   Arb.unit(),
   Arb.unit()
) { a, b, c, d, e, f, g, h, i, j, k, _, _, _ ->
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
): Arb<T> = Arb.bindN(
   genA,
   genB,
   genC,
   genD,
   genE,
   genF,
   genG,
   genH,
   genI,
   genJ,
   genK,
   genL,
   Arb.unit(),
   Arb.unit()
) { a, b, c, d, e, f, g, h, i, j, k, l, _, _ ->
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
): Arb<T> = Arb.bindN(
   genA,
   genB,
   genC,
   genD,
   genE,
   genF,
   genG,
   genH,
   genI,
   genJ,
   genK,
   genL,
   genM,
   Arb.unit()
) { a, b, c, d, e, f, g, h, i, j, k, l, m, _ ->
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
): Arb<T> = Arb.bindN(
   genA,
   genB,
   genC,
   genD,
   genE,
   genF,
   genG,
   genH,
   genI,
   genJ,
   genK,
   genL,
   genM,
   genN,
   bindFn
)

internal fun Arb.Companion.unit(): Arb<Unit> = arbitrary { }

internal fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, T> Arb.Companion.bindN(
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
   bindFn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> T,
): Arb<T> {

   fun <S> shrink(tree: RTree<S>): List<RTree<S>> = tree.children.value

   fun combineShrinks(
      a: RTree<A>, b: RTree<B>, c: RTree<C>, d: RTree<D>, e: RTree<E>,
      f: RTree<F>, g: RTree<G>, h: RTree<H>, i: RTree<I>, j: RTree<J>,
      k: RTree<K>, l: RTree<L>, m: RTree<M>, n: RTree<N>
   ): RTree<T> =
      RTree(
         {
            bindFn(
               a.value(), b.value(), c.value(), d.value(), e.value(),
               f.value(), g.value(), h.value(), i.value(), j.value(),
               k.value(), l.value(), m.value(), n.value()
            )
         },
         kotlin.lazy {
            shrink(a).map { combineShrinks(it, b, c, d, e, f, g, h, i, j, k, l, m, n) } +
            shrink(b).map { combineShrinks(a, it, c, d, e, f, g, h, i, j, k, l, m, n) } +
            shrink(c).map { combineShrinks(a, b, it, d, e, f, g, h, i, j, k, l, m, n) } +
            shrink(d).map { combineShrinks(a, b, c, it, e, f, g, h, i, j, k, l, m, n) } +
            shrink(e).map { combineShrinks(a, b, c, d, it, f, g, h, i, j, k, l, m, n) } +
            shrink(f).map { combineShrinks(a, b, c, d, e, it, g, h, i, j, k, l, m, n) } +
            shrink(g).map { combineShrinks(a, b, c, d, e, f, it, h, i, j, k, l, m, n) } +
            shrink(h).map { combineShrinks(a, b, c, d, e, f, g, it, i, j, k, l, m, n) } +
            shrink(i).map { combineShrinks(a, b, c, d, e, f, g, h, it, j, k, l, m, n) } +
            shrink(j).map { combineShrinks(a, b, c, d, e, f, g, h, i, it, k, l, m, n) } +
            shrink(k).map { combineShrinks(a, b, c, d, e, f, g, h, i, j, it, l, m, n) } +
            shrink(l).map { combineShrinks(a, b, c, d, e, f, g, h, i, j, k, it, m, n) } +
            shrink(m).map { combineShrinks(a, b, c, d, e, f, g, h, i, j, k, l, it, n) } +
            shrink(n).map { combineShrinks(a, b, c, d, e, f, g, h, i, j, k, l, m, it) }
         }
      )


   val arbA = genA.toArb()
   val arbB = genB.toArb()
   val arbC = genC.toArb()
   val arbD = genD.toArb()
   val arbE = genE.toArb()
   val arbF = genF.toArb()
   val arbG = genG.toArb()
   val arbH = genH.toArb()
   val arbI = genI.toArb()
   val arbJ = genJ.toArb()
   val arbK = genK.toArb()
   val arbL = genL.toArb()
   val arbM = genM.toArb()
   val arbN = genN.toArb()

   return object : Arb<T>() {

      override fun edgecase(rs: RandomSource): Sample<T>? {
         return bindFn(
            arbA.edgecase(rs)?.value ?: arbA.next(rs),
            arbB.edgecase(rs)?.value ?: arbB.next(rs),
            arbC.edgecase(rs)?.value ?: arbC.next(rs),
            arbD.edgecase(rs)?.value ?: arbD.next(rs),
            arbE.edgecase(rs)?.value ?: arbE.next(rs),
            arbF.edgecase(rs)?.value ?: arbF.next(rs),
            arbG.edgecase(rs)?.value ?: arbG.next(rs),
            arbH.edgecase(rs)?.value ?: arbH.next(rs),
            arbI.edgecase(rs)?.value ?: arbI.next(rs),
            arbJ.edgecase(rs)?.value ?: arbJ.next(rs),
            arbK.edgecase(rs)?.value ?: arbK.next(rs),
            arbL.edgecase(rs)?.value ?: arbL.next(rs),
            arbM.edgecase(rs)?.value ?: arbM.next(rs),
            arbN.edgecase(rs)?.value ?: arbN.next(rs),
         ).asSample()
      }

      // TODO: all permutations
      override fun allEdgeCases() = listOf<T>()

      override fun sample(rs: RandomSource): Sample<T> {
         val (av, ar) = arbA.sample(rs)
         val (bv, br) = arbB.sample(rs)
         val (cv, cr) = arbC.sample(rs)
         val (dv, dr) = arbD.sample(rs)
         val (ev, er) = arbE.sample(rs)
         val (fv, fr) = arbF.sample(rs)
         val (gv, gr) = arbG.sample(rs)
         val (hv, hr) = arbH.sample(rs)
         val (iv, ir) = arbI.sample(rs)
         val (jv, jr) = arbJ.sample(rs)
         val (kv, kr) = arbK.sample(rs)
         val (lv, lr) = arbL.sample(rs)
         val (mv, mr) = arbM.sample(rs)
         val (nv, nr) = arbN.sample(rs)

         return Sample(
            bindFn(av, bv, cv, dv, ev, fv, gv, hv, iv, jv, kv, lv, mv, nv),
            combineShrinks(ar, br, cr, dr, er, fr, gr, hr, ir, jr, kr, lr, mr, nr)
         )
      }
   }
}

fun <A, B> Arb.Companion.bind(arbs: List<Arb<A>>, fn: (List<A>) -> B): Arb<B> = bind(arbs).map(fn)

internal fun <T> Gen<T>.toArb(): Arb<T> = when (this) {
   is Arb -> this
   is Exhaustive -> this.toArb()
}

internal fun <A> Arb.Companion.bind(arbs: List<Arb<A>>): Arb<List<A>> = when (arbs.size) {
   0 -> Arb.constant(emptyList())
   1 -> arbs[0].map { listOf(it) }
   else -> {
      val listOfArbs: List<Arb<List<A>>> = arbs.chunked(14) { el ->
         check(el.size <= 14) { "reached an impossible state" }

         when (el.size) {
            0 -> Arb.constant(emptyList())
            1 -> el[0].map { listOf(it) }
            2 -> Arb.bind(el[0], el[1]) { a, b -> listOf(a, b) }
            3 -> Arb.bind(el[0], el[1], el[2]) { a, b, c -> listOf(a, b, c) }
            4 -> Arb.bind(el[0], el[1], el[2], el[3]) { a, b, c, d -> listOf(a, b, c, d) }
            5 -> Arb.bind(el[0], el[1], el[2], el[3], el[4]) { a, b, c, d, e -> listOf(a, b, c, d, e) }
            6 -> Arb.bind(el[0], el[1], el[2], el[3], el[4], el[5]) { a, b, c, d, e, f ->
               listOf(a, b, c, d, e, f)
            }
            7 -> Arb.bind(el[0], el[1], el[2], el[3], el[4], el[5], el[6]) { a, b, c, d, e, f, g ->
               listOf(a, b, c, d, e, f, g)
            }
            8 -> Arb.bind(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7]) { a, b, c, d, e, f, g, h ->
               listOf(a, b, c, d, e, f, g, h)
            }
            9 -> Arb.bind(el[0], el[1], el[2], el[3], el[4], el[5], el[6], el[7], el[8]) { a, b, c, d, e, f, g, h, i ->
               listOf(a, b, c, d, e, f, g, h, i)
            }
            10 -> Arb.bind(
               el[0], el[1], el[2], el[3], el[4],
               el[5], el[6], el[7], el[8], el[9]
            ) { a, b, c, d, e, f, g, h, i, j ->
               listOf(a, b, c, d, e, f, g, h, i, j)
            }
            11 -> Arb.bind(
               el[0], el[1], el[2], el[3], el[4],
               el[5], el[6], el[7], el[8], el[9],
               el[10]
            ) { a, b, c, d, e, f, g, h, i, j, k ->
               listOf(a, b, c, d, e, f, g, h, i, j, k)
            }
            12 -> Arb.bind(
               el[0], el[1], el[2], el[3], el[4],
               el[5], el[6], el[7], el[8], el[9],
               el[10], el[11]
            ) { a, b, c, d, e, f, g, h, i, j, k, l ->
               listOf(a, b, c, d, e, f, g, h, i, j, k, l)
            }
            13 -> Arb.bind(
               el[0], el[1], el[2], el[3], el[4],
               el[5], el[6], el[7], el[8], el[9],
               el[10], el[11], el[12]
            ) { a, b, c, d, e, f, g, h, i, j, k, l, m ->
               listOf(a, b, c, d, e, f, g, h, i, j, k, l, m)
            }
            14 -> Arb.bind(
               el[0], el[1], el[2], el[3], el[4],
               el[5], el[6], el[7], el[8], el[9],
               el[10], el[11], el[12], el[13]
            ) { a, b, c, d, e, f, g, h, i, j, k, l, m, n ->
               listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
            }
            else -> Arb.constant(emptyList())
         }
      }

      Arb.bind(listOfArbs).map { it.flatten() }
   }
}
