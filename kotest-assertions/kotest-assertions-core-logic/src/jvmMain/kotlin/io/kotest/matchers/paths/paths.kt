package io.kotest.matchers.paths

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

infix fun Path.shouldStartWithPath(file: File) = this should startWithPath(file)
infix fun Path.shouldNotStartWithPath(file: File) = this shouldNot startWithPath(file)

infix fun Path.shouldStartWithPath(prefix: String) = this should startWithPath(prefix)
infix fun Path.shouldNotStartWithPath(prefix: String) = this shouldNot startWithPath(prefix)

infix fun Path.shouldStartWithPath(path: Path) = this should startWithPath(path)
infix fun Path.shouldNotStartWithPath(path: Path) = this shouldNot startWithPath(path)

fun startWithPath(path: Path) = startWithPath(path.toString())
fun startWithPath(file: File) = startWithPath(file.toPath())
fun startWithPath(prefix: String) = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult = MatcherResult(
      value.toString().startsWith(prefix),
      { "Path $value should start with $prefix" },
      { "Path $value should not start with $prefix" })
}

fun Path.shouldExist() = this should exist()
fun Path.shouldNotExist() = this shouldNot exist()
fun exist() = object : Matcher<Path> {
   override fun test(value: Path) =
      MatcherResult(
         Files.exists(value),
         { "Path $value should exist" },
         { "Path $value should not exist" })
}

infix fun Path.shouldHaveFileSize(size: Long) = this should haveFileSize(size)
infix fun Path.shouldNotHaveFileSize(size: Long) = this shouldNot haveFileSize(size)
fun haveFileSize(size: Long): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult = MatcherResult(
      Files.size(value) == size,
      { "Path $value should have size $size" },
      { "Path $value should not have size $size" })
}


fun Path.shouldBeADirectory() = this should aDirectory()
fun Path.shouldNotBeADirectory() = this shouldNot aDirectory()
fun aDirectory(): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult = MatcherResult(
      Files.isDirectory(value),
      { "File $value should be a directory" },
      { "File $value should not be a directory" })
}

fun Path.shouldBeAFile() = this should aFile()
fun Path.shouldNotBeAFile() = this shouldNot aFile()
fun aFile(): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult = MatcherResult(
      value.isRegularFile(),
      { "Path $value should be a regular file" },
      { "Path $value should not be a regular file" })
}

fun Path.shouldBeAbsolute() = this should beAbsolute()
fun Path.shouldNotBeAbsolute() = this shouldNot beAbsolute()
fun beAbsolute(): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult =
      MatcherResult(
         value.isAbsolute,
         { "Path $value should be absolute" },
         { "Path $value should not be absolute" })
}

fun Path.shouldBeRelative() = this should beRelative()
fun Path.shouldNotBeRelative() = this shouldNot beRelative()
fun beRelative(): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult =
      MatcherResult(
         !value.isAbsolute,
         { "Path $value should be relative" },
         { "Path $value should not be relative" })
}

fun Path.shouldBeReadable() = this should beReadable()
fun Path.shouldNotBeReadable() = this shouldNot beReadable()
fun beReadable(): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult =
      MatcherResult(
         Files.isReadable(value),
         { "Path $value should be readable" },
         { "Path $value should not be readable" }
      )
}

fun Path.shouldBeWriteable() = this should beWriteable()
fun Path.shouldNotBeWriteable() = this shouldNot beWriteable()
fun beWriteable(): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult =
      MatcherResult(
         Files.isWritable(value),
         { "Path $value should be writeable" },
         { "Path $value should not be writeable" }
      )
}

fun Path.shouldBeExecutable() = this should beExecutable()
fun Path.shouldNotBeExecutable() = this shouldNot beExecutable()
fun beExecutable(): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult = MatcherResult(
      Files.isExecutable(value),
      { "Path $value should be executable" },
      { "Path $value should not be executable" }
   )
}

fun Path.shouldBeHidden() = this should beHidden()
fun Path.shouldNotBeHidden() = this shouldNot beHidden()
fun beHidden(): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult =
      MatcherResult(
         Files.isHidden(value),
         { "Path $value should be hidden" },
         { "Path $value should not be hidden" })
}

infix fun Path.shouldContainFileDeep(name: String) = this should containFileDeep(name)
infix fun Path.shouldNotContainFileDeep(name: String) = this shouldNot containFileDeep(name)
fun containFileDeep(name: String): Matcher<Path> = object : Matcher<Path> {

   private fun fileExists(dir: Path): Boolean {
      val contents = dir.listDirectoryEntries()
      val (dirs, files) = contents.partition { it.isDirectory() }
      return files.map { it.name }.contains(name) || dirs.any { fileExists(it) }
   }

   override fun test(value: Path): MatcherResult = MatcherResult(
      fileExists(value),
      { "Path $name should exist in $value" },
      { "Path $name should not exist in $value" }
   )
}

infix fun Path.shouldHaveParent(name: String) = this should haveParent(name)
infix fun Path.shouldNotHaveParent(name: String) = this shouldNot haveParent(name)
fun haveParent(name: String) = object : Matcher<Path> {

   private fun isParentEqualExpected(parent: Path?): Boolean {
      if (parent == null) return false
      return parent.fileName?.toString() == name || isParentEqualExpected(parent.parent)
   }

   override fun test(value: Path) = MatcherResult(
      isParentEqualExpected(value.parent),
      { "Path $value should have parent $name" },
      { "Path $value should not have parent $name" }
   )
}
