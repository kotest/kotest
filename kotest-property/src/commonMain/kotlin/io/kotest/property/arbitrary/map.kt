package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.map

/**
 * Returns a new [Arb] which takes its elements from the receiver and maps them using the supplied function.
 */
fun <A, B> Arb<A>.map(f: (A) -> B): Arb<B> = object : Arb<B>() {

   override fun edgeCase(rs: RandomSource): B? = this@map.edgeCase(rs)?.let(f)

   override fun sample(rs: RandomSource): Sample<B> =
      this@map.sample(rs).let {
         Sample(f(it.value), it.shrinks.map(f))
      }
}

/**
 * Returns a new [Arb] which takes its elements from the receiver and maps them using the supplied function.
 */
fun <A, B> Arb<A>.flatMap(f: (A) -> Arb<B>): Arb<B> = object : Arb<B>() {

   override fun edgeCase(rs: RandomSource): B? {
      // generate an edge case, map it to another arb, and generate an edge case again
      val a = this@flatMap.edgeCase(rs) ?: this@flatMap.next(rs)
      return f(a).edgeCase(rs)
   }

   override fun sample(rs: RandomSource): Sample<B> = f(this@flatMap.sample(rs).value).sample(rs)
}
