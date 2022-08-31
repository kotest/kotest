package io.kotest.data

import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe

// TODO I'm only supporting table with 3 arguments until the API stabilizes

fun <A, B, C> table(
   headers: Headers3,
   fileContent: String,
   transform: (String, String, String) -> Row3<A, B, C>
): Table3<A, B, C> = parseTableContent(headers.values(), fileContent).let { matrix ->
   val rows = matrix.map { row -> transform(row[0], row[1], row[2]) }
   table(headers, *rows.toTypedArray())
}

internal fun parseTableContent(headers: List<String>, fileContent: String) : List<List<String>> {
   val table = StringTable(headers, fileContent.lines())
   return emptyList()
}

internal data class StringTable(
   val headers: List<String>,
   val lines: List<String>,
) {
   val rows: List<List<String>> = lines.map(this::parseRow)
   init {
      rowsShouldHaveSize(headers.size)
   }

   private fun rowsShouldHaveSize(size: Int) {
      val invalid = rows.withIndex()
         .filter { it.value.size != size }
         .map { it.index }
      if (invalid.isNotEmpty()) fail("Expected all rows to have size $size, but got rows at lines $invalid")
   }

   private fun parseRow(value: String): List<String> =
      value.split("|")
         .map(String::trim)
}
