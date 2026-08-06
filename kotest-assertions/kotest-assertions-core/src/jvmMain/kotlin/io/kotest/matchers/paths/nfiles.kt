package io.kotest.matchers.paths

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory

@IgnorableReturnValue
infix fun Path.shouldContainNFiles(n: Int) = this should containNFiles(n)
@IgnorableReturnValue
infix fun Path.shouldNotContainNFiles(n: Int) = this shouldNot containNFiles(n)

fun containNFiles(n: Int): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult {
      val isDir = value.isDirectory()
      val count = if (isDir) Files.newDirectoryStream(value).use { it.count() } else 0
      return MatcherResult(
         isDir && count == n,
         { "$value should be a directory and contain $n files (isDir = $isDir; file count = $count)" },
         { "$value should not be a directory containing $n files" }
      )
   }
}
