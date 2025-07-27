package io.kotest.assertions.print

import io.kotest.assertions.AssertionsConfig
import io.kotest.assertions.ConfigValue

class ListPrint<T>(
   private val limitConfigValue: ConfigValue<Int> = AssertionsConfig.maxCollectionPrintSize,
) : Print<List<T>> {

   override fun print(a: List<T>, level: Int): Printed {
      return if (a.isEmpty()) Printed("[]") else {
         val limit = limitConfigValue.value
         val remainingItems = a.size - limit
         val limitHint =
            if (limitConfigValue.sourceDescription == null) "" else " (set ${limitConfigValue.sourceDescription} to see more / less items)"

         return a.joinToString(
            separator = ", ",
            prefix = "[",
            postfix = "]",
            limit = limit,
            truncated = "...and $remainingItems more$limitHint"
         ) {
            when {
               it is Iterable<*> && it.toList() == a && a.size == 1 -> a[0].toString()
               else -> recursiveRepr(a, it, level).value
            }
         }.printed()
      }
   }
}
