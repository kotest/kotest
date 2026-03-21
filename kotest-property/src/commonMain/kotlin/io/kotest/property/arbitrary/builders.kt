package io.kotest.property.arbitrary

import io.kotest.common.DelicateKotest
import io.kotest.common.KotestInternal
import io.kotest.property.Arb
import io.kotest.property.Classifier
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.Shrinker
import io.kotest.property.asSample
import io.kotest.property.sampleOf
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import io.kotest.property.arbitrary.drop as unboundDrop
import io.kotest.property.arbitrary.next as unboundNext
import io.kotest.property.arbitrary.single as unboundSingle
import io.kotest.property.arbitrary.take as unboundTake

/**
 * Creates a new [Arb] that performs no shrinking, has no edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(fn: suspend ArbitraryBuilderContext.(RandomSource) -> A): Arb<A> =
   arbitraryBuilder { rs -> fn(rs) }

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker], has no edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(shrinker: Shrinker<A>, fn: suspend ArbitraryBuilderContext.(RandomSource) -> A): Arb<A> =
   arbitraryBuilder(shrinker) { rs -> fn(rs) }

/**
 * Creates a new [Arb] that classifies the generated values using the supplied [Classifier], has no edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(classifier: Classifier<A>, fn: suspend ArbitraryBuilderContext.(RandomSource) -> A): Arb<A> =
   arbitraryBuilder(null, classifier) { rs -> fn(rs) }

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker],
 * classifies the generated values using the supplied [Classifier], has no edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(
   shrinker: Shrinker<A>,
   classifier: Classifier<A>,
   fn: suspend ArbitraryBuilderContext.(RandomSource) -> A
): Arb<A> =
   arbitraryBuilder(shrinker, classifier) { rs -> fn(rs) }

/**
 * Creates a new [Arb] that performs no shrinking, uses the given edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(edgecases: List<A>, fn: suspend ArbitraryBuilderContext.(RandomSource) -> A): Arb<A> =
   object : Arb<A>() {
      override fun edgecase(rs: RandomSource): Sample<A>? = if (edgecases.isEmpty()) null else edgecases.random(rs.random).asSample()
      override fun sample(rs: RandomSource): Sample<A> = delegate.sample(rs)

      private val delegate = arbitraryBuilder { rs -> fn(rs) }
   }

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker], uses the given edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(
   edgecases: List<A>,
   shrinker: Shrinker<A>,
   fn: suspend ArbitraryBuilderContext.(RandomSource) -> A
): Arb<A> = object : Arb<A>() {
   override fun edgecase(rs: RandomSource): Sample<A>? = if (edgecases.isEmpty()) null else edgecases.random(rs.random).asSample()
   override fun sample(rs: RandomSource): Sample<A> = delegate.sample(rs)

   private val delegate = arbitraryBuilder(shrinker) { rs -> fn(rs) }
}

/**
 * Creates a new [Arb] that generates edge cases from the given [edgecaseFn] function
 * and generates samples from the given [sampleFn] function.
 */
fun <A> arbitrary(
   edgecaseFn: (RandomSource) -> Sample<A>?,
   sampleFn: suspend ArbitraryBuilderContext.(RandomSource) -> A
): Arb<A> =
   object : Arb<A>() {
      override fun edgecase(rs: RandomSource): Sample<A>? = edgecaseFn(rs)
      override fun sample(rs: RandomSource): Sample<A> = delegate.sample(rs)

      private val delegate: Arb<A> = arbitraryBuilder { rs -> sampleFn(rs) }
   }

/**
 * Creates a new [Arb] that generates edge cases from the given [edgecaseFn] function,
 * performs shrinking using the supplied [Shrinker], and generates samples from the given [sampleFn] function.
 */
fun <A> arbitrary(
   edgecaseFn: (RandomSource) -> Sample<A>?,
   shrinker: Shrinker<A>,
   sampleFn: suspend ArbitraryBuilderContext.(RandomSource) -> A
): Arb<A> =
   object : Arb<A>() {
      override fun edgecase(rs: RandomSource): Sample<A>? = edgecaseFn(rs)
      override fun sample(rs: RandomSource): Sample<A> = delegate.sample(rs)

      private val delegate: Arb<A> = arbitraryBuilder(shrinker) { rs -> sampleFn(rs) }
   }

/**
 * Creates a new [Arb] that performs no shrinking, has no edge cases and
 * generates values from the given function.
 */
suspend inline fun <A> generateArbitrary(
   crossinline fn: suspend GenerateArbitraryBuilderContext.(RandomSource) -> A
): Arb<A> = suspendArbitraryBuilder { rs -> fn(rs) }

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker], has no edge cases and
 * generates values from the given function.
 */
suspend inline fun <A> generateArbitrary(
   shrinker: Shrinker<A>,
   crossinline fn: suspend GenerateArbitraryBuilderContext.(RandomSource) -> A
): Arb<A> = suspendArbitraryBuilder(shrinker, null) { rs -> fn(rs) }

/**
 * Creates a new [Arb] that classifies the generated values using the supplied [Classifier], has no edge cases and
 * generates values from the given function.
 */
suspend inline fun <A> generateArbitrary(
   classifier: Classifier<A>,
   crossinline fn: suspend GenerateArbitraryBuilderContext.(RandomSource) -> A
): Arb<A> = suspendArbitraryBuilder(null, classifier) { rs -> fn(rs) }

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker],
 * classifies the generated values using the supplied [Classifier], has no edge cases and
 * generates values from the given function.
 */
suspend inline fun <A> generateArbitrary(
   shrinker: Shrinker<A>,
   classifier: Classifier<A>,
   crossinline fn: suspend GenerateArbitraryBuilderContext.(RandomSource) -> A
): Arb<A> = suspendArbitraryBuilder(shrinker, classifier) { rs -> fn(rs) }

/**
 * Creates a new [Arb] that performs no shrinking, uses the given edge cases and
 * generates values from the given function.
 */
suspend inline fun <A> generateArbitrary(
   edgecases: List<A>,
   crossinline fn: suspend GenerateArbitraryBuilderContext.(RandomSource) -> A
): Arb<A> = suspendArbitraryBuilder(null, null,
   if (edgecases.isEmpty()) null else { rs -> edgecases.random(rs.random).asSample() }
) { rs -> fn(rs) }

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker], uses the given edge cases and
 * generates values from the given function.
 */
suspend inline fun <A> generateArbitrary(
   edgecases: List<A>,
   shrinker: Shrinker<A>,
   crossinline fn: suspend GenerateArbitraryBuilderContext.(RandomSource) -> A
): Arb<A> = suspendArbitraryBuilder(
   shrinker,
   null,
   if (edgecases.isEmpty()) null else { rs -> edgecases.random(rs.random).asSample() }
) { rs -> fn(rs) }

/**
 * Creates a new [Arb] that generates edge cases from the given [edgecaseFn] function
 * and generates samples from the given [sampleFn] function.
 */
suspend inline fun <A> generateArbitrary(
   crossinline edgecaseFn: (RandomSource) -> Sample<A>?,
   crossinline sampleFn: suspend GenerateArbitraryBuilderContext.(RandomSource) -> A
): Arb<A> {
   val delegate: Arb<A> = suspendArbitraryBuilder { rs -> sampleFn(rs) }

   return object : Arb<A>() {
      override fun edgecase(rs: RandomSource): Sample<A>? = edgecaseFn(rs)
      override fun sample(rs: RandomSource): Sample<A> = delegate.sample(rs)
   }
}

/**
 * Creates a new [Arb] that generates edge cases from the given [edgecaseFn] function,
 * performs shrinking using the supplied [Shrinker], and generates samples from the given [sampleFn] function.
 */
suspend inline fun <A> generateArbitrary(
   crossinline edgecaseFn: (RandomSource) -> Sample<A>?,
   shrinker: Shrinker<A>,
   crossinline sampleFn: suspend GenerateArbitraryBuilderContext.(RandomSource) -> A
): Arb<A> {
   val delegate: Arb<A> = suspendArbitraryBuilder(shrinker) { rs -> sampleFn(rs) }

   return object : Arb<A>() {
      override fun edgecase(rs: RandomSource): Sample<A>? = edgecaseFn(rs)
      override fun sample(rs: RandomSource): Sample<A> = delegate.sample(rs)
   }
}

/**
 * Creates a new [Arb] using [Continuation] using a stateless [builderFn].
 *
 * This function accepts an optional [shrinker], [classifier], and [edgecaseFn]. These parameters
 * will be passed to [ArbitraryBuilder].
 */
fun <A> arbitraryBuilder(
   shrinker: Shrinker<A>? = null,
   classifier: Classifier<A>? = null,
   edgecaseFn: EdgecaseFn<A>? = null,
   builderFn: suspend ArbitraryBuilderContext.(RandomSource) -> A
): Arb<A> = object : Arb<A>() {
   override val classifier: Classifier<out A>? = classifier

   override fun sample(rs: RandomSource): Sample<A> {
      val value = runBuilderFn(SingleShotGenerationMode.Sample, rs)
      return if (shrinker == null) Sample(value) else sampleOf(value, shrinker)
   }

   override fun edgecase(rs: RandomSource): Sample<A> {
      val value = runBuilderFn(SingleShotGenerationMode.Edgecase, rs)
      return edgecaseFn?.invoke(rs) ?: value.asSample()
   }

   /**
    * Runs [builderFn] for one sample/edgecase generation and returns the produced value directly.
    *
    * A fresh [SingleShotArbContinuation] is created on every call because Kotlin [Continuation]s
    * are single-shot and cannot be resumed more than once. The continuation here always completes
    * synchronously — [bind] never actually suspends — so [startCoroutineUninterceptedOrReturn]
    * returns the value inline without ever reaching a suspension point.
    */
   private fun runBuilderFn(mode: SingleShotGenerationMode, rs: RandomSource): A {
      val cont = SingleShotArbContinuation.Restricted(mode, rs) { builderFn(rs) }
      return with(cont) { cont.runToValue() }
   }
}

/**
 * Creates a new suspendable [Arb] using [Continuation] using a stateless [fn].
 *
 * This function accepts an optional [shrinker], [classifier], and [edgecaseFn]. These parameters
 * will be passed to [ArbitraryBuilder].
 */
suspend fun <A> suspendArbitraryBuilder(
   shrinker: Shrinker<A>? = null,
   classifier: Classifier<A>? = null,
   edgecaseFn: EdgecaseFn<A>? = null,
   fn: suspend GenerateArbitraryBuilderContext.(RandomSource) -> A
): Arb<A> = suspendCoroutineUninterceptedOrReturn { cont ->
   val arb = object : Arb<A>() {
      override val classifier: Classifier<out A>? = classifier

      override fun sample(rs: RandomSource): Sample<A> {
         val value = runBuilderFn(SingleShotGenerationMode.Sample, rs)
         return if (shrinker == null) Sample(value) else sampleOf(value, shrinker)
      }

      override fun edgecase(rs: RandomSource): Sample<A>? {
         val value = runBuilderFn(SingleShotGenerationMode.Edgecase, rs)
         return edgecaseFn?.invoke(rs) ?: value.asSample()
      }

      private fun runBuilderFn(genMode: SingleShotGenerationMode, rs: RandomSource): A {
         val c = SingleShotArbContinuation.Suspendedable(genMode, rs, cont.context) { fn(rs) }
         return with(c) { c.runToValue() }
      }
   }

   cont.resume(arb)
}

typealias SampleFn<A> = (RandomSource) -> A
typealias EdgecaseFn<A> = (RandomSource) -> Sample<A>?

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
      if (edgecases.isEmpty()) null else edgecases.random(it.random).asSample()
   }

   fun build() = object : Arb<A>() {
      override val classifier: Classifier<out A>? = this@ArbitraryBuilder.classifier
      override fun edgecase(rs: RandomSource): Sample<A>? = edgecaseFn?.invoke(rs)
      override fun sample(rs: RandomSource): Sample<A> {
         val sample = sampleFn(rs)
         return if (shrinker == null) Sample(sample) else sampleOf(sample, shrinker)
      }
   }
}

interface BaseArbitraryBuilderSyntax {
   /**
    * [bind] returns the generated value of an arb. This can either be a sample or an edgecase.
    */
   suspend fun <T> Arb<T>.bind(): T

   @DelicateKotest
   @Deprecated("Non-deterministic arbitrary usage in arbitrary builder. Use .bind() instead", ReplaceWith("bind()"))
   fun <T> Arb<T>.next(): T = unboundNext()

   @DelicateKotest
   @Deprecated("Non-deterministic arbitrary usage in arbitrary builder. Use .bind() instead", ReplaceWith("bind()"))
   fun <T> Arb<T>.single(): T = unboundSingle()

   @DelicateKotest
   @Deprecated("Non-deterministic arbitrary usage in arbitrary builder. Use .take(count, rs) instead", ReplaceWith("take(count, rs)"))
   fun <A> Arb<A>.take(count: Int): Sequence<A> = unboundTake(count)

   @DelicateKotest
   @Deprecated("Non-deterministic arbitrary usage in arbitrary builder. Use .drop(count, rs) instead", ReplaceWith("drop(count, rs)"))
   fun <A> Arb<A>.drop(count: Int): Sequence<A> = unboundDrop(count)
}

@RestrictsSuspension
interface ArbitraryBuilderContext : BaseArbitraryBuilderSyntax

interface GenerateArbitraryBuilderContext : BaseArbitraryBuilderSyntax

enum class SingleShotGenerationMode { Edgecase, Sample }

/**
 * A [Continuation]-based helper that runs a suspend builder function synchronously.
 *
 * [bind] never actually suspends — it always returns the generated value inline — so
 * [startCoroutineUninterceptedOrReturn] always returns the result directly without ever reaching a
 * real suspension point.  A fresh instance must be created for every sample/edgecase call because
 * Kotlin [Continuation]s are single-shot and cannot be reused.
 *
 * @KotestInternal: this is an implementation detail of the [arbitrary] / [arbitraryBuilder] DSL.
 */
@KotestInternal
sealed class SingleShotArbContinuation<F : BaseArbitraryBuilderSyntax, A>(
   override val context: CoroutineContext,
   private val generationMode: SingleShotGenerationMode,
   private val randomSource: RandomSource,
   private val fn: suspend F.() -> A
) : Continuation<A>, BaseArbitraryBuilderSyntax {

   class Restricted<A>(
      genMode: SingleShotGenerationMode,
      rs: RandomSource,
      fn: suspend ArbitraryBuilderContext.() -> A
   ) : SingleShotArbContinuation<ArbitraryBuilderContext, A>(EmptyCoroutineContext, genMode, rs, fn),
      ArbitraryBuilderContext

   class Suspendedable<A>(
      genMode: SingleShotGenerationMode,
      rs: RandomSource,
      override val context: CoroutineContext,
      fn: suspend GenerateArbitraryBuilderContext.() -> A
   ) : SingleShotArbContinuation<GenerateArbitraryBuilderContext, A>(context, genMode, rs, fn),
      GenerateArbitraryBuilderContext

   private var hasExecuted: Boolean = false

   override fun resumeWith(result: Result<A>) {
      // bind() never actually suspends, so this should never be called in normal usage.
      // If it is called it means some code inside the builder block triggered a real suspension,
      // which is not supported.  Propagate any exception so the failure is visible.
      result.getOrThrow()
   }

   override suspend fun <T> Arb<T>.bind(): T = when (generationMode) {
      SingleShotGenerationMode.Edgecase -> this.edgecase(randomSource)?.value ?: this.sample(randomSource).value
      SingleShotGenerationMode.Sample -> this.sample(randomSource).value
   }

   /**
    * Runs [fn] synchronously and returns the produced value [A].
    *
    * Because [bind] always completes without suspending, [startCoroutineUninterceptedOrReturn]
    * returns the final value inline (never [kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED]).
    * A new [SingleShotArbContinuation] instance must be used for each invocation.
    */
   fun F.runToValue(): A {
      require(!hasExecuted) { "continuation has already been executed, if you see this error please raise a bug report" }
      hasExecuted = true
      val result = fn.startCoroutineUninterceptedOrReturn(this@runToValue, this@SingleShotArbContinuation)
      @Suppress("UNCHECKED_CAST")
      return result as A
   }
}
