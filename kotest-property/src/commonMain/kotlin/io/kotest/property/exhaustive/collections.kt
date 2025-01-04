package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

fun <A> Exhaustive.Companion.collection(collection: Collection<A>): Exhaustive<A> {
   return collection.toList().exhaustive()
}


fun <A> Exhaustive.Companion.permutations(list: List<A>, length: Int = list.size): Exhaustive<List<A>> {
   require(length in 0..list.size) { "length must be between 0 and the list size (${list.size}), but was $length." }

   fun perms(list: List<A>, length: Int): List<List<A>> = buildList {
      when (length) {
         0 -> add(emptyList())
         else -> list.forEach { element ->
            perms(list - element, length - 1).forEach { add(it + element) }
         }
      }
   }

   return perms(list, length).exhaustive()
}

internal fun sampleIndexes(size: Int): Sequence<List<Int>> = sequence {
   require(size > 0) { "Size should be positive, was: $size"}
   val elementsIncluded = MutableList(size) { true }
   val allIndexes = (0 until size).toList()
   yield(allIndexes)
   while(elementsIncluded.any { it }) {
      for (index in 0 until size) {
         if (elementsIncluded[index]) {
            elementsIncluded[index] = false
            yield(allIndexes.filterIndexed { i, _ -> elementsIncluded[i] })
            break
         } else {
            elementsIncluded[index] = true
         }
      }
   }
}
