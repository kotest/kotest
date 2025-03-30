package com.sksamuel.kotest.data

import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.Row3
import io.kotest.data.headers
import io.kotest.data.mapRows
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.data.toTable
import io.kotest.data.writeTable
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import java.io.File

@EnabledIf(LinuxOnlyGithubCondition::class)
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

   context("creating tables from collections") {
      test("create table from map") {
         val map = mapOf(
            "fr" to "French",
            "es" to "Spanish"
         )
         val table = map.toTable(headers("code", "language"))
         table shouldBe table(
            headers("code", "language"),
            row("fr", "French"),
            row("es", "Spanish"),
         )
      }

      val languagesTable = table(
         headers("code", "name", "english"),
         row("fr", "Français", "French"),
         row("es", "Español", "Spanish"),
      )

      data class Language(val code: String, val english: String, val name: String)

      val languages = listOf(
         Language("fr", "French", "Français"),
         Language("es", "Spanish", "Español"),
      )

      test("create table from list") {
         val table = table(
            headers("code", "name", "english"),
            languages.map { row(it.code, it.name, it.english) }
         )
         table shouldBe languagesTable
      }
   }

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
      5  | victor | Victor Hugo | victor.hugo@guernesey.co.uk
      6  | louis    | Louis Caugnault
      7  | edgar
   """.trimIndent()

      val expectedMessage = """
Expected all rows to have 3 columns, but 2 rows differed
- Row 1 has 4 columns: [5, victor, Victor Hugo, victor.hugo@guernesey.co.uk]
- Row 3 has 2 columns: [7, edgar]
      """.trimIndent()

      shouldThrowMessage(expectedMessage) {
         table(headers, invalidRows, transform)
      }
   }

   test("The '|' character can be escaped") {
      val fileContent = """
      1  | prefix\|middle\|suffix | hello\|world
      2  | prefix\suffix  | nothing
      3  | prefix\\|suffix
   """.trimIndent()
      table(headers, fileContent, transform) shouldBe table(
         headers,
         row(1, "prefix|middle|suffix", "hello|world"),
         row(2, "prefix\\suffix", "nothing"),
         row(3, "prefix\\", "suffix"),
      )
   }

   val resourcesDir = File("src/jvmTest/resources/table")

   test("happy path for reading a table from a file") {
      val file = resourcesDir.resolve("users-valid.table")
      table(headers, file, transform) shouldBe expectedTable
   }

   test("Validating table files") {

      shouldThrowMessage("Can't read table file") {
         val file = resourcesDir.resolve("users-does-not-exist.table")
         table(headers, file, transform)
      }

      shouldThrowMessage("Table file must have a .table extension") {
         val file = resourcesDir.resolve("users-invalid-extension.csv")
         table(headers, file, transform)
      }

      shouldThrowMessage("Table file must have a header") {
         val file = resourcesDir.resolve("users-invalid-empty.table")
         table(headers, file, transform)
      }

      shouldThrowMessage(
         """
         Missing elements from index 2
         expected:<["id", "username", "fullName"]> but was:<["id", "username"]>
         """.trimIndent()
      ) {
         val file = resourcesDir.resolve("users-invalid-header.table")
         table(headers, file, transform)
      }
   }

   data class UserInfo(val username: String, val fullName: String)

   val usersTable = table(
      headers("id", "UserInfo"),
      row(4, UserInfo("jmfayard", "Jean-Michel Fayard")),
      row(6, UserInfo("louis", "Louis Caugnault"))
   )

   context("file.writeTable - success") {
      val expectedFileContent = """
id | username | fullName
4  | jmfayard | Jean-Michel Fayard
6  | louis    | Louis Caugnault
      """.trim()

      test("happy path") {
         val file = tempfile(suffix = ".table")
         val rows = usersTable.mapRows { (id, userInfo) ->
            row(id.toString(), userInfo.username, userInfo.fullName)
         }
         val fileContent = file.writeTable(headers("id", "username", "fullName"), rows)
         file.readText() shouldBe expectedFileContent
         fileContent shouldBe expectedFileContent
      }

      test("columns should be aligned") {
         fun row(i: Int): Row3<String, String, String> {
            val value = "$i".repeat(i)
            return row(value, value, value)
         }

         val table = table(
            headers("a", "b", "c"),
            row(2),
            row(4),
            row(6),
         )
         tempfile(suffix = ".table").writeTable(table.headers, table.rows) shouldBe """
a      | b      | c
22     | 22     | 22
4444   | 4444   | 4444
666666 | 666666 | 666666
         """.trimIndent()
      }

      test("| should be escaped") {
         val table = mapOf("greeting" to "Hello || world").toTable()
         val file = tempfile(suffix = ".table")
         file.writeTable(table.headers, table.rows) shouldBe """
            key      | value
            greeting | Hello \|\| world
         """.trimIndent()
      }
   }

   context("file.writeTable - validation") {
      test("Table file must have a table extension") {
         shouldThrowMessage("Table file must have a .table extension") {
            val fileMissingTableExtension = tempfile()
            fileMissingTableExtension.writeTable(usersTable.headers, emptyList())
         }
      }

      test("Cells con't contain new lines") {
         val tableWithNewLines =
            mapOf("1" to "one\n", "two" to "two", "three" to "three\nthree").toTable()
         shouldThrowMessage(testCase.name.name) {
            tempfile(suffix = ".table").writeTable(tableWithNewLines.headers, tableWithNewLines.rows)
         }
      }
   }
})
