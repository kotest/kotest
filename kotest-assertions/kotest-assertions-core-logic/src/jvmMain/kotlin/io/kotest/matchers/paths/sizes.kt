package io.kotest.matchers.paths

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

infix fun Path.shouldBeLarger(other: Path) = this should beLarger(other)
infix fun Path.shouldBeLarger(other: File) = this should beLarger(other.toPath())
infix fun Path.shouldNotBeLarger(other: Path) = this shouldNot beLarger(other)
infix fun Path.shouldNotBeLarger(other: File) = this shouldNot beLarger(other.toPath())

fun beLarger(other: Path): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult {
      val sizea = Files.size(value)
      val sizeb = Files.size(other)
      return MatcherResult(
         sizea > sizeb,
         { "Path $value ($sizea bytes) should be larger than $other ($sizeb bytes)" },
         { "Path $value ($sizea bytes) should not be larger than $other ($sizeb bytes)" })
   }
}

infix fun Path.shouldBeSmaller(other: Path) = this should beSmaller(other)
infix fun Path.shouldBeSmaller(other: File) = this should beSmaller(other.toPath())
infix fun Path.shouldNotBeSmaller(other: Path) = this shouldNot beSmaller(other)
infix fun Path.shouldNotBeSmaller(other: File) = this shouldNot beSmaller(other.toPath())

fun beSmaller(other: Path): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult {
      val sizea = Files.size(value)
      val sizeb = Files.size(other)
      return MatcherResult(
         sizea < sizeb,
         { "Path $value ($sizea bytes) should be smaller than $other ($sizeb bytes)" },
         { "Path $value ($sizea bytes) should not be smaller than $other ($sizeb bytes)" })
   }
}
