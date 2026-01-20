package io.kotest.matchers.collections

//fun<T> sliceStart(list: List<T>, listOffset: Int, slice: List<T>, sliceOffset: Int): Int? {
//   val sliceLength = slice.size - sliceOffset
//   if((list.size - listOffset) < sliceLength) return null
//   (listOffset until list.size - sliceLength + 1).forEach { index ->
//      if ()
//   }
//   return null
//}

internal fun<T> sliceStart(list: List<T>, slice: List<T>): Int? {
   if(list.size  < slice.size) return null
   (0 until list.size - slice.size + 1).forEach { index ->
      val match = slice.withIndex().all { (sliceIndex, sliceValue) ->
         list[index + sliceIndex] == sliceValue
      }
      if (match) return index
   }
   return null
}
