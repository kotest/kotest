package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen

fun <A, B, T> Arb.Companion.zip(
   genA: Gen<A>,
   genB: Gen<B>,
   zipFn: (A, B) -> T
): Arb<T> = Arb.bind(genA, genB, zipFn)

fun <A, B, C, T> Arb.Companion.zip(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   zipFn: (A, B, C) -> T
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
   zipFn(a, b, c)
}

fun <A, B, C, D, T> Arb.Companion.zip(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   zipFn: (A, B, C, D) -> T
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
   zipFn(a, b, c, d)
}

fun <A, B, C, D, E, T> Arb.Companion.zip(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   zipFn: (A, B, C, D, E) -> T
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
   zipFn(a, b, c, d, e)
}

fun <A, B, C, D, E, F, T> Arb.Companion.zip(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   zipFn: (A, B, C, D, E, F) -> T
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
   zipFn(a, b, c, d, e, f)
}

fun <A, B, C, D, E, F, G, T> Arb.Companion.zip(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   zipFn: (A, B, C, D, E, F, G) -> T
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
   zipFn(a, b, c, d, e, f, g)
}

fun <A, B, C, D, E, F, G, H, T> Arb.Companion.zip(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   zipFn: (A, B, C, D, E, F, G, H) -> T
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
   zipFn(a, b, c, d, e, f, g, h)
}

fun <A, B, C, D, E, F, G, H, I, T> Arb.Companion.zip(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   zipFn: (A, B, C, D, E, F, G, H, I) -> T
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
   zipFn(a, b, c, d, e, f, g, h, i)
}

fun <A, B, C, D, E, F, G, H, I, J, T> Arb.Companion.zip(
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
   zipFn: (A, B, C, D, E, F, G, H, I, J) -> T
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
   zipFn(a, b, c, d, e, f, g, h, i, j)
}

fun <A, B, C, D, E, F, G, H, I, J, K, T> Arb.Companion.zip(
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
   zipFn: (A, B, C, D, E, F, G, H, I, J, K) -> T
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
   zipFn(a, b, c, d, e, f, g, h, i, j, k)
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, T> Arb.Companion.zip(
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
   zipFn: (A, B, C, D, E, F, G, H, I, J, K, L) -> T
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
   zipFn(a, b, c, d, e, f, g, h, i, j, k, l)
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, T> Arb.Companion.zip(
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
   zipFn: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> T
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
   zipFn(a, b, c, d, e, f, g, h, i, j, k, l, m)
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, T> Arb.Companion.zip(
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
   zipFn: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> T
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
   zipFn
)

fun <A, B> Arb.Companion.zip(arbs: List<Arb<A>>, fn: (List<A>) -> B): Arb<B> = bind(arbs).map(fn)
