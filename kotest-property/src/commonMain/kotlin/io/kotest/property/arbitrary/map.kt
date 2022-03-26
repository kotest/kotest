package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.map

/**
 * Returns a new [Arb] which takes its elements from the receiver and maps them using the supplied function.
 */
fun <A, B> Arb<A>.map(fn: (A) -> B): Arb<B> = trampoline { sampleA ->
   object : Arb<B>() {
      override fun edgecase(rs: RandomSource): B? = fn(sampleA.value)
      override fun sample(rs: RandomSource): Sample<B> {
         val value = fn(sampleA.value)
         val shrinks = sampleA.shrinks.map(fn)
         return Sample(value, shrinks)
      }
   }
}

/**
 * Returns a new [Arb] which takes its elements from the receiver and maps them using the supplied function.
 */
fun <A, B> Arb<A>.flatMap(fn: (A) -> Arb<B>): Arb<B> = trampoline { fn(it.value) }

/**
 * Returns a new [TrampolineArb] from the receiver [Arb] which composes the operations of [next] lambda
 * using a trampoline method. This allows [next] function to be executed without exhausting call stack.
 */
internal fun <A, B> Arb<A>.trampoline(next: (Sample<A>) -> Arb<B>): Arb<B> = when (this) {
   is TrampolineArb -> thunk(next)
   else -> TrampolineArb(this).thunk(next)
}

/**
 * The [TrampolineArb] is a special Arb that exchanges call stack with heap.
 * In a nutshell, this arb stores command chains to be applied to the original arb inside a list.
 * This technique is an imperative reduction of Free Monads. This eliminates the need of creating intermediate
 * Trampoline Monad and tail-recursive function on those which can be expensive.
 * This minimizes the amount of code and unnecessary object allocation during sample generation in the expense of typesafety.
 *
 * This is an internal implementation. Do not use this TrampolineArb as is and please do not expose this
 * to users outside of the library. For library maintainers, please use the [Arb.trampoline] extension function.
 * The extension function will provide some type-guardrails to workaround the loss of types within this Arb.
 */
@Suppress("UNCHECKED_CAST")
internal class TrampolineArb<A>(val first: Arb<A>) : Arb<A>() {
   private val commandList: MutableList<(Sample<Any>) -> Arb<Any>> = mutableListOf()

   fun <A, B> thunk(fn: (Sample<A>) -> Arb<B>): TrampolineArb<B> {
      val nextFn: (Sample<A>) -> Arb<B> = { fn(it) }
      commandList.add(nextFn as (Sample<Any>) -> Arb<Any>)
      return this as TrampolineArb<B>
   }

   override fun edgecase(rs: RandomSource): A? {
      var currentArb = first as Arb<Any>
      for (command in commandList) {
         val currentEdge = currentArb.edgecase(rs) ?: currentArb.sample(rs).value
         currentArb = command(Sample(currentEdge))
      }

      return currentArb.edgecase(rs) as A?
   }

   override fun sample(rs: RandomSource): Sample<A> {
      var currentArb = first as Arb<Any>
      for (command in commandList) {
         val currentSample = currentArb.sample(rs)
         currentArb = command(currentSample)
      }

      return currentArb.sample(rs) as Sample<A>
   }
}
