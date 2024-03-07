package io.kotest.matchers.file

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

fun File.shouldHaveSameContentAs(other: File, charset: Charset = Charset.forName("utf8")) =
   this should object : Matcher<File> {
      override fun test(value: File): MatcherResult {

         val lines1 = BufferedReader(InputStreamReader(FileInputStream(value), charset))
         val lines2 = BufferedReader(InputStreamReader(FileInputStream(other), charset))

         var passed = true
         var index = 0
         var a: String? = ""
         var b: String? = ""
         while (passed && a != null && b != null) {
            a = lines1.readLine()
            b = lines2.readLine()
            passed = a == b
            index++
         }

         lines1.close()
         lines2.close()

         val diff = when {
            a == null -> "File $other has additional lines, starting at line $index: $b"
            b == null -> "File $value has additional lines, starting at line $index: $a"
            else -> "Instead they differ at line $index:\n+ $a\n- $b"
         }

         return MatcherResult(
            passed,
            { "Files $value and $other should have the same content.\n$diff" },
            { "Files $value and $other should not have the same content" }
         )
      }
   }
