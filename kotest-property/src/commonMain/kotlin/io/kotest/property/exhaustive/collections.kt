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
