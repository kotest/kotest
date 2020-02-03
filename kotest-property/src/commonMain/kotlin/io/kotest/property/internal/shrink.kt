package io.kotest.property.internal

import io.kotest.assertions.show.show
import io.kotest.property.PropertyTesting
import io.kotest.property.ShrinkingMode
import kotlin.time.ExperimentalTime

/**
 * Accepts a value of type A and a function that varies in type A (fixed in any other types) and attempts
 * to shrink the value to find the smallest failing case.
 *
 * If a value fails, then that failing value will be used as input to the shrinker to retrieve
 * the next set of candidate values.
 *
 * Once all values from a shrink step pass, we return the previous value as the "smallest" failing case.
 */
@UseExperimental(ExperimentalTime::class)
internal suspend fun <A> doShrinking(
   initial: A,
   shrinks: Sequence<A>,
   mode: ShrinkingMode,
   test: suspend (A) -> Unit
): A {

   val sb = StringBuilder()
   sb.append("Attempting to shrink failed arg ${initial.show()}\n")
   var candidate = initial
   var count = 0
   var passed = false
   val tested = mutableSetOf<A>()

   fun isShrinking(): Boolean = when (mode) {
      ShrinkingMode.Off -> false
      ShrinkingMode.Unbounded -> true
      is ShrinkingMode.Bounded -> count < mode.bound
   }

   shrinks
      .takeWhile { !passed }
      .takeWhile { isShrinking() }
      .filterNot { tested.contains(it) }
      .forEach {
         tested.add(it)
         count++
         try {
            test(it)
            sb.append("Shrink #$count: ${it.show()} pass\n")
            passed = true
         } catch (t: Throwable) {
            sb.append("Shrink #$count: ${it.show()} fail\n")
            candidate = it
         }
      }

   result(sb, candidate, count)
   println(sb)
   return candidate
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
