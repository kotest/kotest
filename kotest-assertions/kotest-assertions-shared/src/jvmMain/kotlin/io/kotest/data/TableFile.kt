package io.kotest.data

import io.kotest.matchers.shouldBe
import java.io.File

// TODO I'm only supporting table with 3 arguments until the API stabilizes

fun <A, B, C> table(
   headers: Headers3,
   file: File,
   transform: (String, String, String) -> Row3<A, B, C>
): Table3<A, B, C> {
   val rows = file.readStringTable(headers.values())
      .mapRows { (a, b, c) -> transform(a, b, c) }
   return Table3(headers, rows)
}

fun <A, B, C> Table3<A, B, C>.writeToFile(file: File) {
   val cells = rows.map { row -> row.values().map { it.toString() } }
   writeToFile(file, headers.values(), cells)
}

internal fun File.readStringTable(headers: List<String>): StringTable {
   if (exists().not()) throw AssertionError("Can't read table file")
   if (extension != "table") throw AssertionError("Table file must have a .table extension")
   val lines = readLines()
   if (lines.isEmpty()) throw AssertionError("Table file must have a header")
   StringTable.parseRow(lines.first()) shouldBe headers
   return StringTable(headers, lines, skipFirstLine = true)
}

fun writeToFile(file: File, headers: List<String>, cells: List<List<String>>) {
   val separator = " | "
   val formattedHeader = headers.joinToString(separator)
   val formattedContent = cells.joinToString("\n") { row ->
      row.joinToString(separator)
   }

   file.writeText("""
$formattedHeader
$formattedContent
   """.trimIndent())
}
