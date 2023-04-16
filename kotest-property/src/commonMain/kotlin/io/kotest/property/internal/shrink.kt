package io.kotest.property.internal

import io.kotest.assertions.print.print
import io.kotest.mpp.stacktraces
import io.kotest.property.PropertyContext
import io.kotest.property.PropertyTesting
import io.kotest.property.RTree
import io.kotest.property.ShrinkingMode
import io.kotest.property.isEmpty

/**
 * Accepts a value of type A and a function that varies in type A (fixed in any other types) and attempts
 * to shrink the value to find the smallest failing case.
 *
 * For each step in the shrinker, we test all the values. If they all pass then the shrinking ends.
 * Otherwise, the next batch is taken and the shrinks continue.
 *
 * Once all values from a shrink step pass, we return the previous value as the "smallest" failing case
 * along with the reason for the failure.
 *
 */
suspend fun <A> doShrinking(
   initial: RTree<A>,
   mode: ShrinkingMode,
   test: suspend (A) -> Unit
): ShrinkResult<A> {

   if (initial.isEmpty()) return ShrinkResult(initial.value(), initial.value(), null)

   val counter = Counter()
   val tested = mutableSetOf<A>()
   val sb = StringBuilder()
   sb.append("Attempting to shrink arg ${initial.value().print().value}\n")

   val stepResult = doStep(initial, mode, tested, counter, test, sb)
   result(sb, stepResult, counter.count)

   return if (stepResult == null) {
      ShrinkResult(initial.value(), initial.value(), null)
   } else {
      ShrinkResult(initial.value(), stepResult.failed, stepResult.cause)
   }
}

class Counter {
   var count = 0
   fun inc() = count++
}

/**
 * The result of shrinking a failed arg.
 * If no shrinking took place, shrink should be set to the same as iniital
 */
data class ShrinkResult<out A>(val initial: A, val shrink: A, val cause: Throwable?)

data class StepResult<A>(val failed: A, val cause: Throwable)

/**
 * Performs shrinking on the given RTree. Recurses into the tree for failing cases.
 * Returns the last candidate to fail as a [StepResult] or null if the initial passes.
 */
suspend fun <A> doStep(
   tree: RTree<A>,
   mode: ShrinkingMode,
   tested: MutableSet<A>,
   counter: Counter,
   test: suspend (A) -> Unit,
   sb: StringBuilder
): StepResult<A>? {

   // if no more shrinking return null (if we've hit the bounds)
   if (!mode.isShrinking(counter.count)) return null
   val candidates = tree.children.value

   candidates.asSequence()
      // shrinkers might generate duplicate candidates so we must filter them out to avoid infinite loops or slow shrinking
      .filter { tested.add(it.value()) }
      .forEach { a ->
         val candidate = a.value()
         counter.inc()
         try {
            test(candidate)
            if (PropertyTesting.shouldPrintShrinkSteps)
               sb.append("Shrink #${counter.count}: ${candidate.print().value} pass\n")
         } catch (t: Throwable) {
            if (PropertyTesting.shouldPrintShrinkSteps)
               sb.append("Shrink #${counter.count}: ${candidate.print().value} fail\n")
            // this result failed, so we'll recurse in to find further failures otherwise return this candidate
            return doStep(a, mode, tested, counter, test, sb) ?: StepResult(candidate, t)
         }
      }

   return null
}

/**
 * Returns true if we should continue shrinking given the count.
 */
private fun ShrinkingMode.isShrinking(count: Int): Boolean = when (this) {
   ShrinkingMode.Off -> false
   ShrinkingMode.Unbounded -> true
   is ShrinkingMode.Bounded -> count < bound
}

private fun <A> result(sb: StringBuilder, result: StepResult<A>?, count: Int) {
   if (count == 0 || result == null) {
      sb.append("Arg was not shunk\n")
   } else {
      sb.append("Shrink result (after $count shrinks) => ${result.failed.print().value}\n\n")
      when (val location = stacktraces.throwableLocation(result.cause, 4)) {
         null -> sb.append("Caused by ${result.cause}\n")
         else -> {
            sb.append("Caused by ${result.cause} at\n")
            location.forEach { sb.append("\t$it\n") }
         }
      }
   }
   if (PropertyTesting.shouldPrintShrinkSteps) {
      println(sb)
   }
}

/**
 * The contextual shrinking is  only used for output purposes for the time being.
 * Contextual arb shrinking is theoretically possible. A strategy that may work is to treat shrinks
 * as a standard Vector of arbitrary dimension. However, this will need to be discussed and addressed separately.
 */
internal suspend fun PropertyContext.doContextualShrinking(
   mode: ShrinkingMode,
   test: suspend PropertyContext.() -> Unit
): List<ShrinkResult<*>> = generatedSamples().map {
   ShrinkResult(it.value, it.value, null)
}

