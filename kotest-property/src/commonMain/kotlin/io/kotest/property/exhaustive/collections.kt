package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

fun <A> Exhaustive.Companion.collection(collection: Collection<A>): Exhaustive<A> {
   return collection.toList().exhaustive()
}


fun <A> Exhaustive.Companion.permutations(list: List<A>): Exhaustive<List<A>> {
   require(list.isNotEmpty()) { "Can't build an Exhaustive for an empty list." }

   fun perms(list: List<A>): List<List<A>> = when {
      list.isEmpty() -> emptyList()
      list.size == 1 -> listOf(list)
      else -> {
         val result = mutableListOf<List<A>>()
         for (i in list.indices) {
            perms(list - list[i]).forEach { result.add(it + list[i]) }
         }
         result.toList()
      }
   }

   return perms(list).exhaustive()
}
