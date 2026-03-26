package io.kotest.common

fun powerSetIndexes(size: Int): Sequence<List<Int>> = sequence {
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
