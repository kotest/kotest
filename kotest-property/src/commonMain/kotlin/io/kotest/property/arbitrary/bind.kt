package io.kotest.property.arbitrary

import io.kotest.fp.Option
import io.kotest.fp.some
import io.kotest.property.Arb
import io.kotest.property.Edgecase
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.RandomSource
import io.kotest.property.Sample

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
   bindFn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> T
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
      override fun edgecases(): List<T> = emptyList()

      override fun edges(): Option<Edgecase<T>> = Edgecase { rs, config ->
         val a = arbA.edgeOrSample().generate(rs, config)
         val b = arbB.edgeOrSample().generate(rs, config)
         val c = arbC.edgeOrSample().generate(rs, config)
         val d = arbD.edgeOrSample().generate(rs, config)
         val e = arbE.edgeOrSample().generate(rs, config)
         val f = arbF.edgeOrSample().generate(rs, config)
         val g = arbG.edgeOrSample().generate(rs, config)
         val h = arbH.edgeOrSample().generate(rs, config)
         val i = arbI.edgeOrSample().generate(rs, config)
         val j = arbJ.edgeOrSample().generate(rs, config)
         val k = arbK.edgeOrSample().generate(rs, config)
         val l = arbL.edgeOrSample().generate(rs, config)
         val m = arbM.edgeOrSample().generate(rs, config)
         val n = arbN.edgeOrSample().generate(rs, config)

         bindFn(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
      }.some()

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

      private fun <X> Arb<X>.edgeOrSample(): Edgecase<X> = Edgecase { rs, config ->
         val p = rs.random.nextDouble(0.0, 1.0)
         val edgeX: X = this@edgeOrSample.edges().fold(
            { this.next(rs) },
            { it.generate(rs, config) }
         )
         if (p < config.determinism) edgeX else this.next(rs)
      }
   }
}

private fun <T> Gen<T>.toArb(): Arb<T> = when (this) {
   is Arb -> this
   is Exhaustive -> this.toArb()
}
