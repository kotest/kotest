package io.kotest.assertions.eq

import io.kotest.assertions.failure
import io.kotest.assertions.show.show

internal object MapEq : Eq<Map<*, *>?> {
   override fun equals(actual: Map<*, *>?, expected: Map<*, *>?): Throwable? {
      return when {
         actual == null && expected == null -> null
         actual != null && expected != null -> {
            val haveUnequalKeys = eq(actual.keys, expected.keys)
            if(haveUnequalKeys != null) generateError(actual, expected)
            else {
               val hasDifferentValue = actual.keys.any { key ->
                  eq(actual[key], expected[key]) != null
               }
               if(hasDifferentValue) generateError(actual, expected)
               else null
            }
         }
         else -> generateError(actual, expected)
      }
   }
}

private fun generateError(actual: Map<*, *>?, expected: Map<*, *>?): Throwable {
   return failure(
      buildFailureMessage(
         actual,
         expected
      )
   )
}

private fun buildFailureMessage(actual: Map<*, *>?, expected: Map<*, *>?): String {
   val keysDifferentAt = when {
      actual != null && expected != null -> {
         val keysHavingDifferentValues = actual.keys.filterNot { expected[it] == actual[it] }
         "Values differed at keys ${keysHavingDifferentValues.joinToString(limit = 10)}"
      }
      else -> ""
   }
   return "Expected\n${expected?.toFormattedString()}\nto be equal to\n${actual?.toFormattedString()}\n$keysDifferentAt"
}


private fun Map<*, *>.toFormattedString(): String {
   if (isEmpty()) return "{}"
   val indentation = "  "
   val newLine = "\n"
   return toList()
      .joinToString(
         separator = ",$newLine",
         prefix = "{$newLine",
         postfix = "$newLine}",
         limit = 10
      ) { "$indentation${it.first.show().value} = ${it.second.show().value}" }
}
