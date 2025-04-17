package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

fun <A> Exhaustive.Companion.collection(collection: Collection<A>): Exhaustive<A> {
   return collection.toList().exhaustive()
}

/*
* Returns an [Exhaustive] which provides all the permutations of elements from the given list.
* Note that the order of permutations is not specified.
* For instance:
* Exhaustive.permutations(listOf(1, 2, 3)).values shouldContainExactlyInAnyOrder listOf(
*    listOf(3, 2, 1),
*    listOf(2, 3, 1),
*    listOf(3, 1, 2),
*    listOf(1, 3, 2),
*    listOf(2, 1, 3),
*    listOf(1, 2, 3)
* )
*/
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

fun <A> Exhaustive.Companion.slices(list: List<A>): Exhaustive<List<A>> {
   require(list.isNotEmpty()) { "List should not be empty." }
   return sliceIndexes(list.size)
      .map { indexes -> list.subList(indexes.first, indexes.second + 1) }.toList()
      .exhaustive()
}

internal fun sliceIndexes(size: Int): Sequence<Pair<Int, Int>> = sequence {
   require(size > 0) { "Size should be positive, was: $size"}
   (0 until size).flatMap { start ->
      (start until size).map { end -> start to end }
   }.forEach { yield(it) }
}

/*
*  Returns an [Exhaustive] which provides all the subsets of elements from the given list, aka the power set.
* For instance:
* Exhaustive.Companion.samples(listOf("a", "b")).values shouldContainExactlyInAnyOrder
* listOf(
*   listOf(),
*   listOf("a"),
*   listOf("b"),
*   listOf("a", "b"),
* )
 */
fun <A> Exhaustive.Companion.powerSet(list: List<A>): Exhaustive<List<A>> {
   require(list.isNotEmpty()) { "List should not be empty." }
   return powerSetIndexes(list.size)
      .map { indexes -> indexes.map { list[it] } }
      .toList()
      .exhaustive()
}

internal fun powerSetIndexes(size: Int): Sequence<List<Int>> = sequence {
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
