package io.kotlintest.matchers.file

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot
import java.io.File
import java.nio.file.Path

fun File.shouldExist() = this should exist()
fun File.shouldNotExist() = this shouldNot exist()
fun exist() = object : Matcher<File> {
  override fun test(value: File) = Result(value.exists(), "File $value should exist", "File $value should not exist")
}

fun File.shouldHaveExtension(vararg exts: String) = this should haveExtension(*exts)
fun File.shouldNotHaveExtension(vararg exts: String) = this shouldNot haveExtension(*exts)
fun haveExtension(vararg exts: String) = object : Matcher<File> {
  override fun test(value: File) = Result(exts.any { value.name.endsWith(it) }, "File $value should end with one of ${exts.joinToString(",")}", "File $value should not end with one of ${exts.joinToString(",")}")
}

fun File.shouldBeADirectory() = this should aDirectory()
fun File.shouldNotBeADirectory() = this shouldNot aDirectory()
fun aDirectory(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.isDirectory, "File $value should be a directory", "File $value should not be a directory")
}

fun File.shouldBeAFile() = this should aFile()
fun File.shouldNotBeAFile() = this shouldNot aFile()
fun aFile(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.isFile, "File $value should be a file", "File $value should not be a file")
}

fun File.shouldBeAbsolute() = this should beAbsolute()
fun File.shouldNotBeAbsolute() = this shouldNot beAbsolute()
fun beAbsolute(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.isAbsolute, "File $value should be absolute", "File $value should not be absolute")
}

fun File.shouldBeRelative() = this should beRelative()
fun File.shouldNotBeRelative() = this shouldNot beRelative()
fun beRelative(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(!value.isAbsolute, "File $value should be relative", "File $value should not be relative")
}

fun File.shouldBeReadable() = this should beReadable()
fun File.shouldNotBeReadable() = this shouldNot beReadable()
fun beReadable(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.canRead(), "File $value should be readable", "File $value should not be readable")
}

fun File.shouldBeWriteable() = this should beWriteable()
fun File.shouldNotBeWriteable() = this shouldNot beWriteable()
fun beWriteable(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.canWrite(), "File $value should be writeable", "File $value should not be writeable")
}

fun File.shouldBeExecutable() = this should beExecutable()
fun File.shouldNotBeExecutable() = this shouldNot beExecutable()
fun beExecutable(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.canExecute(), "File $value should be executable", "File $value should not be executable")
}

fun File.shouldBeHidden() = this should beHidden()
fun File.shouldNotBeHidden() = this shouldNot beHidden()
fun beHidden(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.isHidden, "File $value should be hidden", "File $value should not be hidden")
}

fun File.shouldStartWithPath(path: Path) = this should startWithPath(path)
fun File.shouldNotStartWithPath(path: Path) = this shouldNot startWithPath(path)

fun File.shouldStartWithPath(prefix: String) = this should startWithPath(prefix)
fun File.shouldNotStartWithPath(prefix: String) = this shouldNot startWithPath(prefix)

fun File.shouldStartWithPath(file: File) = this should startWithPath(file)
fun File.shouldNotStartWithPath(file: File) = this shouldNot startWithPath(file)

fun startWithPath(path: Path) = startWithPath(path.toFile())
fun startWithPath(file: File) = startWithPath(file.toString())
fun startWithPath(prefix: String) = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.toString().startsWith(prefix), "File $value should start with $prefix", "File $value should not start with $prefix")
}

