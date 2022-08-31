package com.sksamuel.kotest.data

import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe

class StringTableTest : FunSpec({

   val headers = headers("id", "username", "fullName")
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

   test("happy path") {
      table(headers, validFileContent, transform) shouldBe expectedTable
   }

   test("empty lines and comments starting with # are accepted") {
      val fileContent = """
         |4  | jmfayard | Jean-Michel Fayard
         |# this is a comment
         |# newlines are allowed
         |
         |6  | louis    | Louis Caugnault
      """.trimMargin()
      val table = table(headers, fileContent, transform)

      table shouldBe expectedTable
   }

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

   test("The '|' character can be escaped") {
      val fileContent = """
      1  | bad \| good | name
   """.trimIndent()
      table(headers, fileContent, transform) shouldBe table(
         headers,
         row(1, "bad | good", "name")
      )
   }

})
