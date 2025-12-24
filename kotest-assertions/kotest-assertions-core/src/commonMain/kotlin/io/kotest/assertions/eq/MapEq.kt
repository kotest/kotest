package io.kotest.assertions.eq

import io.kotest.assertions.AssertionsConfig
import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.Expected
import io.kotest.assertions.print.Printed
import io.kotest.assertions.print.print

/**
 * An [Eq] for [Map] types which compares the keys and values of the maps.
 */
internal object MapEq : Eq<Map<*, *>> {

   @Deprecated("Use the overload with one more parameter of type EqContext.")
   override fun equals(actual: Map<*, *>, expected: Map<*, *>, strictNumberEq: Boolean): Throwable? =
      equals(actual, expected, strictNumberEq, EqContext())

   override fun equals(actual: Map<*, *>, expected: Map<*, *>, strictNumberEq: Boolean, context: EqContext): Throwable? {
      // If both references point to the same object, they're equal (handles cyclic references)
      if (actual === expected) return null

      // Check for cycles - if we've already visited this pair, consider them equal to break the cycle
      if (context.isVisited(actual, expected)) return null

      context.push(actual, expected)
      try {
         val haveUnequalKeys = EqCompare.compare(actual.keys, expected.keys, strictNumberEq, context)

         return if (haveUnequalKeys != null) generateError(actual, expected)
         else {
            val hasDifferentValue = actual.keys.any { key ->
               EqCompare.compare(actual[key], expected[key], strictNumberEq, context) != null
            }
            if (hasDifferentValue) generateError(actual, expected)
            else null
         }
      } finally {
         context.pop()
      }
   }
}

private fun generateError(actual: Map<*, *>?, expected: Map<*, *>?): Throwable {
   return AssertionErrorBuilder.create()
      .withMessage(buildFailureMessage(actual, expected))
      .withValues(Expected(print(expected)), Actual(print(actual)))
      .build()
}

private fun buildFailureMessage(actual: Map<*, *>?, expected: Map<*, *>?): String {
   return when {
      actual != null && expected != null -> {
         val keysHavingDifferentValues = actual.keys.filterNot { expected[it] == actual[it] }
         "Values differed at keys ${keysHavingDifferentValues.joinToString(limit = AssertionsConfig.mapDiffLimit)}\n"
      }

      else -> ""
   }
}


private fun print(map: Map<*, *>?): Printed {
   if (map == null) return null.print()
   if (map.isEmpty()) return Printed("{}")
   val indentation = "  "
   val newLine = "\n"
   return Printed(
      map.toList()
         .joinToString(
            separator = ",$newLine",
            prefix = "{$newLine",
            postfix = "$newLine}",
            limit = AssertionsConfig.mapDiffLimit,
         ) { "$indentation${it.first.print().value} = ${it.second.print().value}" }
   )
}
