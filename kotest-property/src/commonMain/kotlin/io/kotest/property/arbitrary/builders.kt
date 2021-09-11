package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Classifier
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.Shrinker
import io.kotest.property.sampleOf
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine

/**
 * Creates a new [Arb] that performs no shrinking, has no edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(fn: suspend ArbitraryBuilderSyntax.(RandomSource) -> A): Arb<A> =
   arbitraryBuilder { rs -> fn(rs) }

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker], has no edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(shrinker: Shrinker<A>, fn: suspend ArbitraryBuilderSyntax.(RandomSource) -> A): Arb<A> =
   arbitraryBuilder(shrinker) { rs -> fn(rs) }

/**
 * Creates a new [Arb] that classifies the generated values using the supplied [Classifier], has no edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(classifier: Classifier<A>, fn: suspend ArbitraryBuilderSyntax.(RandomSource) -> A): Arb<A> =
   arbitraryBuilder(null, classifier) { rs -> fn(rs) }

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker],
 * classifies the generated values using the supplied [Classifier], has no edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(
   shrinker: Shrinker<A>,
   classifier: Classifier<A>,
   fn: suspend ArbitraryBuilderSyntax.(RandomSource) -> A
): Arb<A> =
   arbitraryBuilder(shrinker, classifier) { rs -> fn(rs) }

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

fun <A> arbitraryBuilder(
   shrinker: Shrinker<A>? = null,
   classifier: Classifier<A>? = null,
   edgecaseFn: EdgecaseFn<A>? = null,
   builderFn: suspend ArbitraryBuilderSyntax.(RandomSource) -> A
): Arb<A> = object : Arb<A>() {
   override fun edgecase(rs: RandomSource): A? = computeArb().edgecase(rs)
   override fun sample(rs: RandomSource): Sample<A> = computeArb().sample(rs)

   private fun computeArb(): Arb<A> {
      val continuation = ArbContinuation<A>()
      val wrapReturn: suspend ArbitraryBuilderSyntax.() -> Arb<A> = {
         val value: A = builderFn(randomSource.bind())
         ArbitraryBuilder(
            { value },
            classifier,
            shrinker,
            { rs ->
               // use edgecase function if provided, otherwise retain those from flatmap compositions
               if (edgecaseFn != null) edgecaseFn.invoke(rs) else value
            }
         ).build()
      }
      wrapReturn.startCoroutine(continuation, continuation)
      return continuation.returnedArb()
   }

   // passthrough arb to extract the propagated RandomSource
   private val randomSource: Arb<RandomSource> = ArbitraryBuilder.create { it }.withEdgecaseFn { it }.build()
}

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

@RestrictsSuspension
interface ArbitraryBuilderSyntax {
   suspend fun <T> Arb<T>.bind(): T
}

private class ArbContinuation<A> : Continuation<Arb<A>>, ArbitraryBuilderSyntax {
   override val context: CoroutineContext = EmptyCoroutineContext
   private lateinit var returnedArb: Arb<A>

   fun returnedArb(): Arb<A> = returnedArb

   override fun resumeWith(result: Result<Arb<A>>) = result.map { returnedArb = it }.getOrThrow()

   override suspend fun <B> Arb<B>.bind(): B = suspendCoroutineUninterceptedOrReturn { c ->
      returnedArb = this.flatMap { b: B ->
         c.resume(b)
         returnedArb
      }
      COROUTINE_SUSPENDED
   }
}
