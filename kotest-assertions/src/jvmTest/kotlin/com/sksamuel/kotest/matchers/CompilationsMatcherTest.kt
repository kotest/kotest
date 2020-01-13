package com.sksamuel.kotest.matchers

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldCompiles
import io.kotest.matchers.shouldNotCompiles
import java.io.File

class CompilationsMatcherTest : StringSpec() {
   lateinit var file: File

   override fun beforeTest(testCase: TestCase) {
      file = File("codeSnippet.kt")
   }

   override fun afterTest(testCase: TestCase, result: TestResult) {
      file.delete()
   }

   init {
      "a code snippet of valid variable assignment should compile" {
         val codeSnippet = """
             val aString: String = "A valid variable assignment"
          """.trimIndent()

         codeSnippet.shouldCompiles()
         file.writeText(codeSnippet)
         file.shouldCompiles()
      }

      "a code snippet of invalid variable assignment should not compile" {
         val codeSnippet = """
             val aString: String = 123
          """.trimIndent()

         codeSnippet.shouldNotCompiles()
         file.writeText(codeSnippet)
         file.shouldNotCompiles()
      }

      "a code snippet with missing variable assignment should not compile" {
         val codeSnippet = """
            package org.bar.foo

            fun foo() {
               val aLocalDate: LocalDate = LocalDate.now()
               println(aLocalDate)
            }
          """.trimIndent()

         codeSnippet.shouldNotCompiles()
         file.writeText(codeSnippet)
         file.shouldNotCompiles()
      }

      "a code snippet with a proper import statement should compile" {
         val codeSnippet = """
            package org.bar.foo
            import java.time.LocalDate

            fun foo() {
               val aLocalDate: LocalDate = LocalDate.now()
               println(aLocalDate)
            }
          """.trimIndent()

         codeSnippet.shouldCompiles()
         file.writeText(codeSnippet)
         file.shouldCompiles()
      }

      "a code snippet with a invalid import statement should not compile" {
         val codeSnippet = """
            package org.bar.foo
            import foo.time.LocalDate

            fun foo() {
               val aLocalDate: LocalDate = LocalDate.now()
               println(aLocalDate)
            }
          """.trimIndent()

         codeSnippet.shouldNotCompiles()
         file.writeText(codeSnippet)
         file.shouldNotCompiles()
      }
   }
}
