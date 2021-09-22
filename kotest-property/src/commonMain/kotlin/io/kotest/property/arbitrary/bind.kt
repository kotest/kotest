package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.Shrinker
import io.kotest.property.rtree
import io.kotest.property.sampleOf

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

private fun Arb.Companion.unit(): Arb<Unit> = arbitrary { }

private fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, T> Arb.Companion.bindN(
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

      override fun edgecase(rs: RandomSource): T? {
         return bindFn(
            arbA.edgecase(rs) ?: arbA.next(rs),
            arbB.edgecase(rs) ?: arbB.next(rs),
            arbC.edgecase(rs) ?: arbC.next(rs),
            arbD.edgecase(rs) ?: arbD.next(rs),
            arbE.edgecase(rs) ?: arbE.next(rs),
            arbF.edgecase(rs) ?: arbF.next(rs),
            arbG.edgecase(rs) ?: arbG.next(rs),
            arbH.edgecase(rs) ?: arbH.next(rs),
            arbI.edgecase(rs) ?: arbI.next(rs),
            arbJ.edgecase(rs) ?: arbJ.next(rs),
            arbK.edgecase(rs) ?: arbK.next(rs),
            arbL.edgecase(rs) ?: arbL.next(rs),
            arbM.edgecase(rs) ?: arbM.next(rs),
            arbN.edgecase(rs) ?: arbN.next(rs),
         )
      }

      override fun sample(rs: RandomSource): Sample<T> {
         val (av, `as`) = arbA.sample(rs)
         val (bv, bs) = arbB.sample(rs)
         val (cv, cs) = arbC.sample(rs)
         val (dv, ds) = arbD.sample(rs)
         val (ev, es) = arbE.sample(rs)
         val (fv, fs) = arbF.sample(rs)
         val (gv, gs) = arbG.sample(rs)
         val (hv, hs) = arbH.sample(rs)
         val (iv, `is`) = arbI.sample(rs)
         val (jv, js) = arbJ.sample(rs)
         val (kv, ks) = arbK.sample(rs)
         val (lv, ls) = arbL.sample(rs)
         val (mv, ms) = arbM.sample(rs)
         val (nv, ns) = arbN.sample(rs)

         // Shrink components one by one
         val shrinker = Shrinker { _: T ->
            listOf(
               bindFn(`as`.value(), bv, cv, dv, ev, fv, gv, hv, iv, jv, kv, lv, mv, nv),
               bindFn(av, bs.value(), cv, dv, ev, fv, gv, hv, iv, jv, kv, lv, mv, nv),
               bindFn(av, bv, cs.value(), dv, ev, fv, gv, hv, iv, jv, kv, lv, mv, nv),
               bindFn(av, bv, cv, ds.value(), ev, fv, gv, hv, iv, jv, kv, lv, mv, nv),
               bindFn(av, bv, cv, dv, es.value(), fv, gv, hv, iv, jv, kv, lv, mv, nv),
               bindFn(av, bv, cv, dv, ev, fs.value(), gv, hv, iv, jv, kv, lv, mv, nv),
               bindFn(av, bv, cv, dv, ev, fv, gs.value(), hv, iv, jv, kv, lv, mv, nv),
               bindFn(av, bv, cv, dv, ev, fv, gv, hs.value(), iv, jv, kv, lv, mv, nv),
               bindFn(av, bv, cv, dv, ev, fv, gv, hv, `is`.value(), jv, kv, lv, mv, nv),
               bindFn(av, bv, cv, dv, ev, fv, gv, hv, iv, js.value(), kv, lv, mv, nv),
               bindFn(av, bv, cv, dv, ev, fv, gv, hv, iv, jv, ks.value(), lv, mv, nv),
               bindFn(av, bv, cv, dv, ev, fv, gv, hv, iv, jv, kv, ls.value(), mv, nv),
               bindFn(av, bv, cv, dv, ev, fv, gv, hv, iv, jv, kv, lv, ms.value(), nv),
               bindFn(av, bv, cv, dv, ev, fv, gv, hv, iv, jv, kv, lv, mv, ns.value()),
            )
         }

         return sampleOf(
            bindFn(av, bv, cv, dv, ev, fv, gv, hv, iv, jv, kv, lv, mv, nv),
            shrinker
         )
      }
   }
}

fun <A, B> Arb.Companion.bind(arbs: List<Arb<A>>, fn: (List<A>) -> B): Arb<B> = bind(arbs).map(fn)

private fun <T> Gen<T>.toArb(): Arb<T> = when (this) {
   is Arb -> this
   is Exhaustive -> this.toArb()
}

private fun <A> Arb.Companion.bind(arbs: List<Arb<A>>): Arb<List<A>> = when (arbs.size) {
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
