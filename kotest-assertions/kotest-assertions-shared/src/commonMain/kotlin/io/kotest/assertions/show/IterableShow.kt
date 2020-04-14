package io.kotest.assertions.show

class IterableShow<T> : Show<Iterable<T>> {
   override fun show(a: Iterable<T>): Printed = ListShow<T>().show(a.toList())
}

class ListShow<T> : Show<List<T>> {

   private val maxCollectionSnippetSize = 20

   override fun show(a: List<T>): Printed {
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
               else -> recursiveRepr(a, it).value
            }
         }.printed()
      }
   }
}
