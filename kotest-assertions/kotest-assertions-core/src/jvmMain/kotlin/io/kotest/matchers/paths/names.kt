package io.kotest.matchers.paths

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.nio.file.Path

fun Path.shouldHaveExtension(vararg exts: String) = this should haveExtension(*exts)
fun Path.shouldNotHaveExtension(vararg exts: String) = this shouldNot haveExtension(*exts)
fun haveExtension(vararg exts: String) = object : Matcher<Path> {
   override fun test(value: Path) = MatcherResult(
      exts.any { value.fileName.toString().endsWith(it) },
      { "Path $value should end with one of ${exts.joinToString(",")}" },
      {
         "Path $value should not end with one of ${exts.joinToString(",")}"
      })
}


infix fun Path.shouldHaveNameWithoutExtension(name: String) = this should haveNameWithoutExtension(name)
infix fun Path.shouldNotHaveNameWithoutExtension(name: String) = this shouldNot haveNameWithoutExtension(name)
fun haveNameWithoutExtension(name: String) = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult {
      val filename = value.fileName.toString()
      val actual = if (filename.contains(".")) filename.split('.').dropLast(1).joinToString(".") else filename
      return MatcherResult(
         actual == name,
         { "Path $value should have name without extension of $name but was $actual" },
         {
            "Path $value should not have name without extension of $name"
         })
   }
}
