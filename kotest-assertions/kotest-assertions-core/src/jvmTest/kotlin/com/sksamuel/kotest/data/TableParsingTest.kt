package com.sksamuel.kotest.data

import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table

class TableParsingTest : FunSpec({

   val headers = headers("id", "username", "fullName")
   val headersText = "id | username | fullName"
   val transform = { a: String, b: String, c: String ->
      row(a.toInt(), b, c)
   }
   val expectedTable = table(
      headers,
      row(4, "jmfayard", "Jean-Michel Fayard"),
      row(6, "louis", "Louis Caugnault"),
   )

   val validFileContent = """
      4  | jmfayard | Jean-Michel Fayard
      6  | louis    | Louis Caugnault
   """.trimIndent()


   test("All rows must have the right number of columns") {
      val invalidRows = """
      4  | jmfayard | Jean-Michel Fayard
      5  | victor | Victor Hugo | victor.hugo@guernesey.co.k
      6  | louis    | Louis Caugnault
      7  | edgar
   """.trimIndent()
      shouldThrowMessage("Expected all rows to have size 3, but got rows at lines [1, 3]") {
         table(headers, invalidRows, transform)
      }
   }

})
