package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionsConfig
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.print.Printed
import io.kotest.assertions.print.print

internal object MapEq : Eq<Map<*, *>?> {
   override fun equals(actual: Map<*, *>?, expected: Map<*, *>?, strictNumberEq: Boolean): Throwable? {
      return when {
         actual == null && expected == null -> null
         actual != null && expected != null -> {
            val haveUnequalKeys = eq(actual.keys, expected.keys, strictNumberEq)
            if (haveUnequalKeys != null) generateError(actual, expected)
            else {
               val hasDifferentValue = actual.keys.any { key ->
                  eq(actual[key], expected[key], strictNumberEq) != null
               }
               if (hasDifferentValue) generateError(actual, expected)
               else null
            }
         }

         else -> generateError(actual, expected)
      }
   }
}

private fun generateError(actual: Map<*, *>?, expected: Map<*, *>?): Throwable {
   return failure(
      Expected(print(expected)),
      Actual(print(actual)),
      buildFailureMessage(
         actual,
         expected
      )
   )
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
   return Printed(map.toList()
      .joinToString(
         separator = ",$newLine",
         prefix = "{$newLine",
         postfix = "$newLine}",
         limit = AssertionsConfig.mapDiffLimit,
      ) { "$indentation${it.first.print().value} = ${it.second.print().value}" }
   )
}
