package io.kotest.matchers.paths

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory

infix fun Path.shouldContainNFiles(n: Int) = this should containNFiles(n)
infix fun Path.shouldNotContainNFiles(n: Int) = this shouldNot containNFiles(n)

fun containNFiles(n: Int): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult {
      val count = Files.newDirectoryStream(value).use { it.count() }
      return MatcherResult(
         value.isDirectory() && count == n,
         { "$value should be a directory and contain $n files (isDir = ${value.isDirectory()}; file count = $count)" },
         { "$value should not be a directory containing $n files" }
      )
   }
}
