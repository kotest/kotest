package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.RandomSource
import io.kotest.property.Sample

fun <A, B, T> Arb.Companion.bind(
   edgecaseDeterminism: Double,
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
      Arb.unit(),
      edgecaseDeterminism
   ) { a, b, _, _, _, _, _, _, _, _, _, _, _, _ ->
      bindFn(a, b)
   }

fun <A, B, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   bindFn: (A, B) -> T
): Arb<T> = bind(defaultDeterminism, genA, genB, bindFn)

fun <A, B, C, T> Arb.Companion.bind(
   edgecaseDeterminism: Double,
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
   Arb.unit(),
   edgecaseDeterminism
) { a, b, c, _, _, _, _, _, _, _, _, _, _, _ ->
   bindFn(a, b, c)
}

fun <A, B, C, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   bindFn: (A, B, C) -> T
): Arb<T> = bind(defaultDeterminism, genA, genB, genC, bindFn)

fun <A, B, C, D, T> Arb.Companion.bind(
   edgecaseDeterminism: Double,
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
   Arb.unit(),
   edgecaseDeterminism
) { a, b, c, d, _, _, _, _, _, _, _, _, _, _ ->
   bindFn(a, b, c, d)
}

fun <A, B, C, D, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   bindFn: (A, B, C, D) -> T
): Arb<T> = bind(defaultDeterminism, genA, genB, genC, genD, bindFn)

fun <A, B, C, D, E, T> Arb.Companion.bind(
   edgecaseDeterminism: Double,
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
   Arb.unit(),
   edgecaseDeterminism
) { a, b, c, d, e, _, _, _, _, _, _, _, _, _ ->
   bindFn(a, b, c, d, e)
}

fun <A, B, C, D, E, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   bindFn: (A, B, C, D, E) -> T
): Arb<T> = bind(defaultDeterminism, genA, genB, genC, genD, genE, bindFn)

fun <A, B, C, D, E, F, T> Arb.Companion.bind(
   edgecaseDeterminism: Double,
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
   Arb.unit(),
   edgecaseDeterminism
) { a, b, c, d, e, f, _, _, _, _, _, _, _, _ ->
   bindFn(a, b, c, d, e, f)
}

fun <A, B, C, D, E, F, T> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   bindFn: (A, B, C, D, E, F) -> T
): Arb<T> = bind(defaultDeterminism, genA, genB, genC, genD, genE, genF, bindFn)

fun <A, B, C, D, E, F, G, T> Arb.Companion.bind(
   edgecaseDeterminism: Double,
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
   Arb.unit(),
   edgecaseDeterminism
) { a, b, c, d, e, f, g, _, _, _, _, _, _, _ ->
   bindFn(a, b, c, d, e, f, g)
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
): Arb<T> = bind(defaultDeterminism, genA, genB, genC, genD, genE, genF, genG, bindFn)

fun <A, B, C, D, E, F, G, H, T> Arb.Companion.bind(
   edgecaseDeterminism: Double,
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
   Arb.unit(),
   edgecaseDeterminism
) { a, b, c, d, e, f, g, h, _, _, _, _, _, _ ->
   bindFn(a, b, c, d, e, f, g, h)
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
): Arb<T> = bind(defaultDeterminism, genA, genB, genC, genD, genE, genF, genG, genH, bindFn)

fun <A, B, C, D, E, F, G, H, I, T> Arb.Companion.bind(
   edgecaseDeterminism: Double,
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
   Arb.unit(),
   edgecaseDeterminism
) { a, b, c, d, e, f, g, h, i, _, _, _, _, _ ->
   bindFn(a, b, c, d, e, f, g, h, i)
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
): Arb<T> = bind(defaultDeterminism, genA, genB, genC, genD, genE, genF, genG, genH, genI, bindFn)

fun <A, B, C, D, E, F, G, H, I, J, T> Arb.Companion.bind(
   edgecaseDeterminism: Double,
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
   Arb.unit(),
   edgecaseDeterminism
) { a, b, c, d, e, f, g, h, i, j, _, _, _, _ ->
   bindFn(a, b, c, d, e, f, g, h, i, j)
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
): Arb<T> = bind(defaultDeterminism, genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, bindFn)

fun <A, B, C, D, E, F, G, H, I, J, K, T> Arb.Companion.bind(
   edgecaseDeterminism: Double,
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
   Arb.unit(),
   edgecaseDeterminism
) { a, b, c, d, e, f, g, h, i, j, k, _, _, _ ->
   bindFn(a, b, c, d, e, f, g, h, i, j, k)
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
): Arb<T> = bind(defaultDeterminism, genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, bindFn)

fun <A, B, C, D, E, F, G, H, I, J, K, L, T> Arb.Companion.bind(
   edgecaseDeterminism: Double,
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
   Arb.unit(),
   edgecaseDeterminism
) { a, b, c, d, e, f, g, h, i, j, k, l, _, _ ->
   bindFn(a, b, c, d, e, f, g, h, i, j, k, l)
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
): Arb<T> = bind(defaultDeterminism, genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, bindFn)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, T> Arb.Companion.bind(
   edgecaseDeterminism: Double,
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
   Arb.unit(),
   edgecaseDeterminism
) { a, b, c, d, e, f, g, h, i, j, k, l, m, _ ->
   bindFn(a, b, c, d, e, f, g, h, i, j, k, l, m)
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
   bind(defaultDeterminism, genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, bindFn)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, T> Arb.Companion.bind(
   edgecaseDeterminism: Double,
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
   edgecaseDeterminism,
   bindFn
)

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
   bind(defaultDeterminism, genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, genN, bindFn)

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
   edgecaseDeterminism: Double,
   bindFn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> T
): Arb<T> {
   check(edgecaseDeterminism in 0.0..1.0) { "provided edgecaseDeterminism $edgecaseDeterminism is not between 0.0 and 1.0" }

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
      override fun edgecases(): List<T> = emptyList()

      override fun generateEdgecase(rs: RandomSource): T {
         val a = arbA.edgeOrSample(rs)
         val b = arbB.edgeOrSample(rs)
         val c = arbC.edgeOrSample(rs)
         val d = arbD.edgeOrSample(rs)
         val e = arbE.edgeOrSample(rs)
         val f = arbF.edgeOrSample(rs)
         val g = arbG.edgeOrSample(rs)
         val h = arbH.edgeOrSample(rs)
         val i = arbI.edgeOrSample(rs)
         val j = arbJ.edgeOrSample(rs)
         val k = arbK.edgeOrSample(rs)
         val l = arbL.edgeOrSample(rs)
         val m = arbM.edgeOrSample(rs)
         val n = arbN.edgeOrSample(rs)
         return bindFn(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
      }

      override fun sample(rs: RandomSource): Sample<T> {
         val a = arbA.sample(rs).value
         val b = arbB.sample(rs).value
         val c = arbC.sample(rs).value
         val d = arbD.sample(rs).value
         val e = arbE.sample(rs).value
         val f = arbF.sample(rs).value
         val g = arbG.sample(rs).value
         val h = arbH.sample(rs).value
         val i = arbI.sample(rs).value
         val j = arbJ.sample(rs).value
         val k = arbK.sample(rs).value
         val l = arbL.sample(rs).value
         val m = arbM.sample(rs).value
         val n = arbN.sample(rs).value
         return Sample(bindFn(a, b, c, d, e, f, g, h, i, j, k, l, m, n))
      }

      override fun values(rs: RandomSource): Sequence<Sample<T>> = generateSequence { sample(rs) }

      private fun <X> Arb<X>.edgeOrSample(rs: RandomSource): X {
         val p = rs.random.nextDouble(0.0, 1.0)
         return if (p < edgecaseDeterminism) this@edgeOrSample.generateEdgecase(rs) else this.next(rs)
      }
   }
}

private fun <T> Gen<T>.toArb(): Arb<T> = when (this) {
   is Arb -> this
   is Exhaustive -> this.toArb()
}

private const val defaultDeterminism = 0.9
