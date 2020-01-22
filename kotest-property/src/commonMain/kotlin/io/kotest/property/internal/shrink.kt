package io.kotest.property.internal

import io.kotest.assertions.show.show
import io.kotest.property.PropertyTesting
import io.kotest.property.RTree

/**
 * Accepts a failed value and tests it while shrinking it's value.
 *
 * The shrinker will generate a smaller value, which will then be tested using the
 * given test function (which has been fixed in other parameters).
 *
 * If the test fails, the failing value will now be used as the input to the shrinker.
 * If the test now passes, the previous failing value is returned as the 'smallest'
 * failing case.
 */
suspend fun <T> shrink(tree: RTree<T>, test: suspend (T) -> Unit): T {

   val sb = StringBuilder()
   sb.append("Attempting to shrink failed arg ${tree.value.show()}\n")
   var candidate = tree
   val tested = HashSet<T>()
   var count = 0

   val candidates = candidate.children.filterNot { tested.contains(it.value) }

   when {
      // if candidates is empty then that means that there were no further shrinks to test
      candidates.isEmpty() -> return result(sb, candidate.value, count)
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
            null -> return result(sb, candidate.value, count)
            else -> candidate = next
         }
      }
   }

   return candidate.value
}

fun <T> result(sb: StringBuilder, value: T, count: Int): T {
   when (count) {
      0 -> sb.append("Shrink result => ${value.show()}\n")
      else -> sb.append("Shrink result (after $count shrinks) => ${value.show()}\n")
   }
   if (PropertyTesting.shouldPrintShrinkSteps) {
      println(sb)
   }
   return value
}
