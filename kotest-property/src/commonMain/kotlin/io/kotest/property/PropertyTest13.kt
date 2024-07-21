package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.internal.proptest

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> checkAll(
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
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit
): PropertyContext = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, config, property)

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forAll(
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
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
) = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, config) { a, b, c, d, e, f, g, h, i, j, k, l, m -> property(a, b, c, d, e, f, g, h, i, j, k, l, m) shouldBe true }

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> forNone(
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
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Boolean
) = proptest(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL, genM, config) {
      a, b, c, d, e, f, g, h, i, j, k, l, m ->
   property(a, b, c, d, e, f, g, h, i, j, k, l, m) shouldBe false
}
