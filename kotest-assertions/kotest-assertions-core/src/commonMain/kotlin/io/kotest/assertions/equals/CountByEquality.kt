package io.kotest.assertions.equals

fun<T> Iterable<T>.countByEquality(equality: Equality<T>): Map<T, Int> {
   val counts = mutableMapOf<T, Int>()
   val commutativeEquality = CommutativeEquality(equality)
   for(element in this) {
      val equalElement = counts.keys.firstOrNull {
         commutativeEquality.verify(element, it).areEqual()
      }
      if(equalElement != null) {
         counts[equalElement] = (counts[equalElement]!! + 1)
      } else {
         counts[element] = 1
      }
   }
   return counts.toMap()
}

