package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Classifier
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.Shrinker
import io.kotest.property.sampleOf

/**
 * Creates a new [Arb] that performs no shrinking, has no edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(fn: (RandomSource) -> A): Arb<A> =
   arbitrary(emptyList(), fn)

/**
 * Creates a new [Arb] that performs no shrinking, uses the given edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(edgecases: List<A>, fn: (RandomSource) -> A): Arb<A> = object : Arb<A>() {
   override fun edgecase(rs: RandomSource): A? = if (edgecases.isEmpty()) null else edgecases.random(rs.random)
   override fun sample(rs: RandomSource): Sample<A> = Sample(fn(rs))
}

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker], uses the given edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(edgecases: List<A>, shrinker: Shrinker<A>, fn: (RandomSource) -> A): Arb<A> = object : Arb<A>() {
   override fun edgecase(rs: RandomSource): A? = if (edgecases.isEmpty()) null else edgecases.random(rs.random)
   override fun sample(rs: RandomSource): Sample<A> = sampleOf(fn(rs), shrinker)
}

/**
 * Creates a new [Arb] that generates edge cases from the given [edgecaseFn] function
 * and generates samples from the given [sampleFn] function.
 */
fun <A> arbitrary(edgecaseFn: (RandomSource) -> A?, sampleFn: (RandomSource) -> A): Arb<A> =
   object : Arb<A>() {
      override fun edgecase(rs: RandomSource): A? = edgecaseFn(rs)
      override fun sample(rs: RandomSource): Sample<A> = Sample(sampleFn(rs))
   }

/**
 * Creates a new [Arb] that generates edge cases from the given [edgecaseFn] function,
 * performs shrinking using the supplied [Shrinker, and generates samples from the given [sampleFn] function.
 */
fun <A> arbitrary(
   edgecaseFn: (RandomSource) -> A?,
   shrinker: Shrinker<A>,
   sampleFn: (RandomSource) -> A
): Arb<A> =
   object : Arb<A>() {
      override fun edgecase(rs: RandomSource): A? = edgecaseFn(rs)
      override fun sample(rs: RandomSource): Sample<A> = sampleOf(sampleFn(rs), shrinker)
   }

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker], has no edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(shrinker: Shrinker<A>, fn: (RandomSource) -> A): Arb<A> =
   arbitrary(emptyList(), shrinker, fn)

typealias SampleFn<A> = (RandomSource) -> A
typealias EdgecaseFn<A> = (RandomSource) -> A?

class ArbitraryBuilder<A>(
   private val sampleFn: SampleFn<A>,
   private val classifier: Classifier<A>?,
   private val shrinker: Shrinker<A>?,
   private val edgecaseFn: EdgecaseFn<A>?,
) {
   companion object {
      fun <A> create(f: (RandomSource) -> A): ArbitraryBuilder<A> = ArbitraryBuilder(f, null, null, null)
   }

   fun withClassifier(classifier: Classifier<A>) = ArbitraryBuilder(sampleFn, classifier, shrinker, edgecaseFn)
   fun withShrinker(shrinker: Shrinker<A>) = ArbitraryBuilder(sampleFn, classifier, shrinker, edgecaseFn)
   fun withEdgecaseFn(edgecaseFn: EdgecaseFn<A>) = ArbitraryBuilder(sampleFn, classifier, shrinker, edgecaseFn)
   fun withEdgecases(edgecases: List<A>) = ArbitraryBuilder(sampleFn, classifier, shrinker) {
      if (edgecases.isEmpty()) null else edgecases.random(it.random)
   }

   fun build() = object : Arb<A>() {
      override val classifier: Classifier<out A>? = this@ArbitraryBuilder.classifier
      override fun edgecase(rs: RandomSource): A? = edgecaseFn?.invoke(rs)
      override fun sample(rs: RandomSource): Sample<A> {
         val sample = sampleFn(rs)
         return if (shrinker == null) Sample(sample) else sampleOf(sample, shrinker)
      }
   }
}
