package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample

/**
 * Returns a sequence of size [count] from values generated from this arb.
 * Edge cases will be ignored.
 */
fun <A> Arb<A>.take(count: Int, rs: RandomSource = RandomSource.default()): Sequence<A> =
   samples(rs).map { it.value }.take(count)

/**
 * Returns a single value generated from this arb ignoring edge cases.
 * Alias for next.
 */
fun <A> Arb<A>.single(rs: RandomSource = RandomSource.default()): A = this.sample(rs).value

/**
 * Returns a single value generated from this arb ignoring edge cases.
 * Alias for single.
 */
fun <A> Arb<A>.next(rs: RandomSource = RandomSource.default()): A = single(rs)

/**
 * Wraps a [Arb] lazily. The given [f] is only evaluated once,
 * and not until the wrapper [Arb] is evaluated.
 **/
fun <A> Arb.Companion.lazy(f: () -> Arb<A>): Arb<A> {
   val arb by kotlin.lazy { f() }

   return object : Arb<A>() {
      override fun edgecase(rs: RandomSource): Sample<A>? = arb.edgecase(rs)
      override fun sample(rs: RandomSource): Sample<A> = arb.sample(rs)
   }
}
