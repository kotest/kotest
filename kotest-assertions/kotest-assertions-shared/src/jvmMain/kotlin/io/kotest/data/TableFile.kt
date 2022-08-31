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

fun File.writeTable(headers: Headers1, rows: List<Row1<String>>): String =
   writeTable(headers.values(), rows.map { it.strings() } )
fun File.writeTable(headers: Headers2, rows: List<Row2<String, String>>): String =
   writeTable(headers.values(), rows.map { it.strings() } )
fun File.writeTable(headers: Headers3, rows: List<Row3<String, String, String>>): String =
   writeTable(headers.values(), rows.map { it.strings() } )
// TODO

private fun Row.strings(): List<String> = values().map { it.toString() }


internal fun File.readStringTable(headers: List<String>): StringTable {
   if (exists().not()) throw AssertionError("Can't read table file")
   if (extension != "table") throw AssertionError("Table file must have a .table extension")
   val lines = readLines()
   if (lines.isEmpty()) throw AssertionError("Table file must have a header")
   StringTable.parseRow(lines.first()) shouldBe headers
   return StringTable(headers, lines, skipFirstLine = true)
}

fun File.writeTable(headers: List<String>, cells: List<List<String>>): String {
   if (extension != "table") throw AssertionError("Table file must have a .table extension")
   val containsNewLines = cells.any { it.any { cell -> cell.contains("\n") } }
   if (containsNewLines) throw AssertionError("Cells con't contain new lines")
   val separator = " | "
   val formattedHeader = headers.joinToString(separator)
   val formattedContent = cells.joinToString("\n") { row ->
      row.joinToString(separator)
   }

   val fileContent = """
$formattedHeader
$formattedContent
   """.trimIndent()
   writeText(fileContent)
   return fileContent
}
