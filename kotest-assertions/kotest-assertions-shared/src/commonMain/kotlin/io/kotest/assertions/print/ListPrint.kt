package io.kotest.assertions.print

class ListPrint<T> : Print<List<T>> {

   private val maxCollectionSnippetSize = 20

   override fun print(a: List<T>): Printed = print(a, 0)

   override fun print(a: List<T>, level: Int): Printed {
      return if (a.isEmpty()) Printed("[]") else {
         val remainingItems = a.size - maxCollectionSnippetSize

         val suffix = when {
            remainingItems <= 0 -> "]"
            else -> "] and $remainingItems more"
         }

         return a.joinToString(
            separator = ", ",
            prefix = "[",
            postfix = suffix,
            limit = maxCollectionSnippetSize
         ) {
            when {
               it is Iterable<*> && it.toList() == a && a.size == 1 -> a[0].toString()
               else -> recursiveRepr(a, it, level).value
            }
         }.printed()
      }
   }
}
