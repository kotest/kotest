package io.kotest.data

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

      if (invalidRows.isNotEmpty()) fail(
         """
         |Expected all rows to have $size columns, but ${invalidRows.size} rows differed
         |$formattedRows
         |$andMore
         """.trimMargin().trim()
      )
   }

   companion object {
      val separatorRegex = Regex("([\\\\]{2}|[^\\\\])\\|")

      internal fun parseRow(line: String): List<String> {
         val trimmed = line.replace(" ", "")
         return line
            .split(separatorRegex)
            .map {
               val cell = it.trim()
               val suffix = if ("$cell\\\\|" in trimmed) "\\" else ""
               cell.plus(suffix)
                  .replace("\\|", "|")
                  .replace("\\\\", "\\")
         }
      }
   }
}
