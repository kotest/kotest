package io.kotest.data

import io.kotest.assertions.fail

internal data class StringTable(
   val headers: List<String>,
   val lines: List<String>,
) {
   fun <T> mapRows(fn: (List<String>) -> T): List<T> =
      rows.map { fn(it.value) }

   val rows: List<IndexedValue<List<String>>> =
      lines
         .withIndex()
         .filterNot { (_, line) ->
            line.startsWith("#") || line.isBlank()
         }
         .map { it.parseRow() }

   init { rowsShouldHaveSize(headers.size) }

   private fun rowsShouldHaveSize(size: Int) {
      val invalid = rows
         .filter { it.value.size != size }
         .map { it.index }
      if (invalid.isNotEmpty()) fail("Expected all rows to have size $size, but got rows at lines $invalid")
   }

   private fun IndexedValue<String>.parseRow(): IndexedValue<List<String>> {
      val (index, line) = this
      val notAPipeSeparator = "🫓"
      return line
         .replace("\\|", notAPipeSeparator)
         .split("|")
         .map { it.trim().replace(notAPipeSeparator, "|") }
         .let { IndexedValue(index, it) }
   }
}
