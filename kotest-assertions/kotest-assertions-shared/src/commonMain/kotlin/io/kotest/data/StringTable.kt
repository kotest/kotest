package io.kotest.data

import io.kotest.assertions.fail

internal data class StringTable(
   val headers: List<String>,
   val lines: List<String>,
   val skipFirstLine: Boolean = false, // for files
) {

   fun <T> mapRows(fn: (List<String>) -> T): List<T> =
      rows.map { fn(it.value) }

   val rows: List<IndexedValue<List<String>>> =
      lines
         .withIndex()
         .filterNot { (index, line) ->
            val skipHeader = index == 0 && skipFirstLine
            skipHeader || line.startsWith("#") || line.isBlank()
         }
         .map { (index, line) -> IndexedValue(index, parseRow(line)) }

   init {
      rowsShouldHaveSize(headers.size)
   }

   private fun rowsShouldHaveSize(size: Int) {
      val maxRows = 5
      val invalidRows = rows
         .filter { it.value.size != size }
      val formattedRows = invalidRows
         .take(maxRows)
         .joinToString("\n") { (i, row) ->
            "- Row $i has ${row.size} columns: $row"
         }
      val andMore = if (invalidRows.size <= maxRows) "" else "... and ${invalidRows.size - maxRows} other rows"

      if (invalidRows.isNotEmpty()) fail("""
         |Expected all rows to have $size columns, but ${invalidRows.size} rows differed
         |$formattedRows
         |$andMore
         """.trimMargin().trim())
   }

   companion object {
      internal fun parseRow(line: String): List<String> {
         val result = mutableListOf<String>()
         val list = line.split("|")
         val needsMerge = list.withIndex().filter { (i, cell) ->
            cell.endsWith("\\") && cell.endsWith("\\\\").not()
         }.map { it.index }.toSet()

         var current = ""
         list.forEachIndexed { i, cell ->
            if (i in needsMerge) {
               current += cell
                  .removeSuffix("\\")
                  .plus("|")
            } else {
               result += "$current$cell"
                  .replace("\\\\", "\\")
                  .trim()
               current  = ""
            }
         }
         return result
      }
   }
}
