package io.kotest.matchers.paths

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

fun Path.shouldBeEmptyDirectory() = this should beEmptyDirectory()
fun Path.shouldNotBeEmptyDirectory() = this shouldNot beEmptyDirectory()

fun beEmptyDirectory(): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult {
      return if (value.isDirectory()) {
         val count = Files.newDirectoryStream(value).use { it.count() }
         MatcherResult(
            count == 0,
            { "$value should be an empty directory but contained $count file(s)" },
            { "$value should not be a non-empty directory" },
         )
      } else {
         val reason = when {
            value.isRegularFile() -> "was a regular file"
            !value.exists() -> "it does not exist"
            else -> "could not determine type"
         }
         MatcherResult(
            false,
            { "$value should be an empty directory, but $reason" },
            { "$value should not be a non-empty directory, but $reason" },
         )
      }
   }
}
