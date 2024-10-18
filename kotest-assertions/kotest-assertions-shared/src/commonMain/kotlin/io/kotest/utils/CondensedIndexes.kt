package io.kotest.utils

class CondensedIndexes {
}

fun Iterable<Int>.condenseToRanges(minRangeSize: Int): Sequence<IndexesToPrint> {
   val iterator = this.iterator()
   var startOfRange: Int = if(iterator.hasNext()) iterator.next() else return emptySequence()
   var endOfRange: Int = startOfRange
   return sequence {
      while (iterator.hasNext()) {
         val currentIndex = iterator.next()
         if(currentIndex > endOfRange + 1) {
            yieldIndexes(endOfRange, startOfRange, minRangeSize)
            startOfRange = currentIndex
            endOfRange = startOfRange
         } else {
            endOfRange = currentIndex
         }
      }
      yieldIndexes(endOfRange, startOfRange, minRangeSize)
   }
}

private suspend fun SequenceScope<IndexesToPrint>.yieldIndexes(
   endOfRange: Int,
   startOfRange: Int,
   minRangeSize: Int
) {
   if ((endOfRange - startOfRange + 1) >= minRangeSize) {
      yield(IndexesToPrint.RangeOfIndexes(startOfRange, endOfRange))
   } else {
      (startOfRange..endOfRange).forEach { yield(IndexesToPrint.SingleIndex(it)) }
   }
}

sealed interface IndexesToPrint {
   data class SingleIndex(val index: Int) : IndexesToPrint
   data class RangeOfIndexes(val start: Int, val end: Int) : IndexesToPrint {
      override fun toString(): String = "[$start..$end]"
   }
}
