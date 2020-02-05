package io.kotest.property.internal

import io.kotest.assertions.show.show
import io.kotest.property.PropertyTesting
import io.kotest.property.RTree
import io.kotest.property.ShrinkingMode
import kotlin.time.ExperimentalTime

/**
 * Accepts a value of type A and a function that varies in type A (fixed in any other types) and attempts
 * to shrink the value to find the smallest failing case.
 *
 * For each step in the shrinker, we test all the values. If they all pass then the shrinking ends.
 * Otherwise, the next batch is taken and the shrinks continue.
 *
 * Once all values from a shrink step pass, we return the previous value as the "smallest" failing case.
 */
@UseExperimental(ExperimentalTime::class)
internal suspend fun <A> doShrinking(
   initial: RTree<A>,
   mode: ShrinkingMode,
   test: suspend (A) -> Unit
): A {

   val counter = Counter()
   val tested = mutableSetOf<A>()
   val sb = StringBuilder()
   sb.append("Attempting to shrink failed arg ${initial.value.show()}\n")

   val candidate = doStep(initial, mode, tested, counter, test, sb) ?: initial.value
   result(sb, candidate, counter.count)
   println(sb)
   return candidate
}

class Counter {
   var count = 0
   fun inc() = count++
}

/**
 * Performs shrinking on the given RTree. Recurses into the tree for failing cases.
 */
internal suspend fun <A> doStep(
   tree: RTree<A>,
   mode: ShrinkingMode,
   tested: MutableSet<A>,
   counter: Counter,
   test: suspend (A) -> Unit,
   sb: StringBuilder
): A? {

   if (!mode.isShrinking(counter.count)) return null
   val candidates = tree.children.value

   candidates.asSequence()
      .filter { tested.add(it.value) }
      .forEach { a ->
         counter.inc()
         try {
            test(a.value)
            if (PropertyTesting.shouldPrintShrinkSteps)
               sb.append("Shrink #${counter.count}: ${a.show()} pass\n")
         } catch (t: Throwable) {
            if (PropertyTesting.shouldPrintShrinkSteps)
               sb.append("Shrink #${counter.count}: ${a.show()} fail\n")
            // this result failed, so we'll recurse in to find further failures otherwise return this
            return doStep(a, mode, tested, counter, test, sb) ?: a.value
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

private fun <A> result(sb: StringBuilder, candidate: A, count: Int): A {
   when (count) {
      0 -> sb.append("Shrink result => ${candidate.show()}\n")
      else -> sb.append("Shrink result (after $count shrinks) => ${candidate.show()}\n")
   }
   if (PropertyTesting.shouldPrintShrinkSteps) {
      println(sb)
   }
   return candidate
}
