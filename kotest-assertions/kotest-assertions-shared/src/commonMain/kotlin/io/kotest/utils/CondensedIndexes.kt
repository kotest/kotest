package io.kotest.utils

class CondensedIndexes {
}

fun Iterable<Int>.condense(minRangeSize: Int): Sequence<IndexesToPrint> {
   val iterator = this.iterator()
   var startOfRange: Int = if(iterator.hasNext()) iterator.next() else return emptySequence()
   var endOfRange: Int = startOfRange
   return sequence {
      while (iterator.hasNext()) {
         val currentIndex = iterator.next()
         if(currentIndex > endOfRange + 1) {
            if ((endOfRange - startOfRange + 1) >= minRangeSize) {
               yield(IndexesToPrint.RangeOfIndexes(startOfRange, endOfRange))
            } else {
               (startOfRange..endOfRange).forEach { yield(IndexesToPrint.SingleIndex(it)) }
            }
            startOfRange = currentIndex
            endOfRange = startOfRange
         } else {
            endOfRange = currentIndex
         }
      }
      if ((endOfRange - startOfRange + 1) >= minRangeSize) {
         yield(IndexesToPrint.RangeOfIndexes(startOfRange, endOfRange))
      } else {
         (startOfRange..endOfRange).forEach { yield(IndexesToPrint.SingleIndex(it)) }
      }
   }
}

sealed interface IndexesToPrint {
   data class SingleIndex(val index: Int) : IndexesToPrint
   data class RangeOfIndexes(val start: Int, val end: Int) : IndexesToPrint {
      override fun toString(): String = "[$start..$end]"
   }
}
