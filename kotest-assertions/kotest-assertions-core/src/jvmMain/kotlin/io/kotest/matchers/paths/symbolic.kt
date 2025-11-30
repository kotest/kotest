package io.kotest.matchers.paths

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.nio.file.Files
import java.nio.file.Path

fun Path.shouldBeSymbolicLink() = this should beSymbolicLink()
fun Path.shouldNotBeSymbolicLink() = this shouldNot beSymbolicLink()
fun beSymbolicLink() = object : Matcher<Path> {
   override fun test(value: Path) = MatcherResult(
      Files.isSymbolicLink(value),
      { "Path $value should be a symbolic link" },
      { "Path $value should not be a symbolic link" }
   )
}
