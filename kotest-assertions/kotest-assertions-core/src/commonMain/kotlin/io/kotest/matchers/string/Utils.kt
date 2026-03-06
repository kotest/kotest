package io.kotest.matchers.string

fun allIndexesOf(value: String, substring: String): List<Int> {
   val indexes = mutableListOf<Int>()
   var index = value.indexOf(substring)
   while (index >= 0 && indexes.size < 100) {
      indexes.add(index)
      index = value.indexOf(substring, index + 1)
   }
   return indexes
}
