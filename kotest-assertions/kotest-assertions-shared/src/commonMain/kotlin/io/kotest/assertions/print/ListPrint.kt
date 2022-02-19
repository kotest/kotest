package io.kotest.assertions.print

import io.kotest.assertions.AssertionsConfig

class ListPrint<T> : Print<List<T>> {
   override fun print(a: List<T>): Printed = print(a, 0)

   override fun print(a: List<T>, level: Int): Printed {
      return if (a.isEmpty()) Printed("[]") else {
         val limit = AssertionsConfig.maxCollectionPrintSize.value
         val remainingItems = a.size - limit

         return a.joinToString(
            separator = ", ",
            prefix = "[",
            postfix = "]",
            limit = limit,
            truncated = "...and $remainingItems more (set ${AssertionsConfig.maxCollectionPrintSize.sourceDescription} to see more / less items)"
         ) {
            when {
               it is Iterable<*> && it.toList() == a && a.size == 1 -> a[0].toString()
               else -> recursiveRepr(a, it, level).value
            }
         }.printed()
      }
   }
}
