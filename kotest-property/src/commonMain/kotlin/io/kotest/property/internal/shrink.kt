package io.kotest.property.internal

import io.kotest.assertions.show.show
import io.kotest.property.PropertyTesting
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.PropertyInput

/**
 * Accepts a failed value and tests it while shrinking it's value.
 *
 * The shrinker will generate a smaller value, which will then be tested using the
 * given test function (which has been fixed in other parameters).
 *
 * If the test fails, the failing value will now be used as the input to the shrinker.
 * If the test now passes, the previous failing value is returned as the 'smallest'
 * failing case.
 *
 * @param mode specifies the [ShrinkingMode] which determines how many shrink steps should be attempted.
 */
suspend fun <T> shrink(input: PropertyInput<T>, test: suspend (T) -> Unit, mode: ShrinkingMode): T {

   val sb = StringBuilder()
   sb.append("Attempting to shrink failed arg ${input.show()}\n")
   var candidate = input
   val tested = HashSet<T>()
   var count = 0

   while (mode.shouldShrink(count)) {
      val candidates = candidate.candidates().filterNot { tested.contains(it.value) }
      when {
         // if candidates is empty then that means that there were no further shrinks to test
         candidates.isEmpty() -> return result(sb, candidate, count)
         else -> {
            val next = candidates.firstOrNull {
               tested.add(it.value)
               count++
               try {
                  test(it.value)
                  sb.append("Shrink #$count: ${it.show()} pass\n")
                  false
               } catch (t: Throwable) {
                  sb.append("Shrink #$count: ${it.show()} fail\n")
                  true
               }
            }
            when (next) {
               // if next is null, that means all the shrinks passed so the original value is the smallest
               null -> return result(sb, candidate, count)
               else -> candidate = next
            }
         }
      }
   }
   return candidate.value
}

fun <T> result(sb: StringBuilder, candidate: PropertyInput<T>, count: Int): T {
   when (count) {
      0 -> sb.append("Shrink result => ${candidate.value.show()}\n")
      else -> sb.append("Shrink result (after $count shrinks) => ${candidate.show()}\n")
   }
   if (PropertyTesting.shouldPrintShrinkSteps) {
      println(sb)
   }
   return candidate.value
}

fun ShrinkingMode.shouldShrink(count: Int): Boolean = when (this) {
   ShrinkingMode.Off -> false
   ShrinkingMode.Unbounded -> true
   is ShrinkingMode.Bounded -> count <= bound
}

fun <T> PropertyInput<T>.candidates(): List<PropertyInput<T>> = when (this) {
   is PropertyInput.Value<T> -> emptyList()
   is PropertyInput.ValueAndShrinker -> shrinker()
}
