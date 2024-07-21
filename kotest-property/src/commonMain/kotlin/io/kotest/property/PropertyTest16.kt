package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.internal.proptest

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> checkAll(
   config: PropTestConfig,
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
   genO: Gen<O>,
   genP: Gen<P>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> Unit
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, genN, genO, genP, config, property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> forAll(
   config: PropTestConfig = PropTestConfig(),
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
   genO: Gen<O>,
   genP: Gen<P>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> Boolean
) = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, genN, genO, genP, config) { a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p -> property(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) shouldBe true }

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> forNone(
   config: PropTestConfig = PropTestConfig(),
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
   genO: Gen<O>,
   genP: Gen<P>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> Boolean
) = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, genN, genO, genP, config) {
      a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p ->
   property(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) shouldBe false
}
