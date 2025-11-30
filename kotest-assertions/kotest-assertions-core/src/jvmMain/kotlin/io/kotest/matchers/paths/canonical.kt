package io.kotest.matchers.paths

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.nio.file.Path

fun Path.shouldBeCanonical() = this should beCanonicalPath()
fun Path.shouldNotBeCanonical() = this shouldNot beCanonicalPath()
fun beCanonicalPath(): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult {
      return MatcherResult(
         value.toFile().canonicalPath == value.toFile().path,
         { "File $value should be canonical" },
         { "File $value should not be canonical" })
   }
}
