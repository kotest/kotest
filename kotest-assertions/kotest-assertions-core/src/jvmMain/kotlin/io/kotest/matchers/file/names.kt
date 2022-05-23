package io.kotest.matchers.file

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.io.File

fun File.shouldHaveExtension(vararg exts: String) = this should haveExtension(*exts)
fun File.shouldNotHaveExtension(vararg exts: String) = this shouldNot haveExtension(*exts)
fun haveExtension(vararg exts: String) = object : Matcher<File> {
   override fun test(value: File) = MatcherResult(
      exts.any { value.name.endsWith(it) },
      { "File $value should end with one of ${exts.joinToString(",")}" },
      { "File $value should not end with one of ${exts.joinToString(",")}" }
   )
}

infix fun File.shouldHavePath(name: String) = this should havePath(name)
infix fun File.shouldNotHavePath(name: String) = this shouldNot havePath(name)
fun havePath(name: String) = object : Matcher<File> {
   override fun test(value: File) =
      MatcherResult(
         value.path == name,
         { "File $value should have path $name" },
         { "File $value should not have path $name" }
      )
}

infix fun File.shouldHaveName(name: String) = this should haveName(name)
infix fun File.shouldNotHaveName(name: String) = this shouldNot haveName(name)
fun haveName(name: String) = object : Matcher<File> {
   override fun test(value: File) =
      MatcherResult(
         value.name == name,
         { "File $value should have name $name" },
         { "File $value should not have name $name"}
      )
}


infix fun File.shouldHaveNameWithoutExtension(name: String) = this should haveNameWithoutExtension(name)
infix fun File.shouldNotHaveNameWithoutExtension(name: String) = this shouldNot haveNameWithoutExtension(name)
fun haveNameWithoutExtension(name: String) = object : Matcher<File> {
   override fun test(value: File): MatcherResult {
      val actual = if (value.name.contains(".")) value.name.split('.').dropLast(1).joinToString(".") else value.name
      return MatcherResult(
         actual == name,
         { "File $value should have name without extension of $name but was $actual" },
         { "File $value should not have name without extension of $name"}
      )
   }
}
