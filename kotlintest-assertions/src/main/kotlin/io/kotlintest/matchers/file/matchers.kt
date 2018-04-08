package io.kotlintest.matchers.file

import io.kotlintest.Matcher
import io.kotlintest.Result
import java.io.File
import java.nio.file.Path

fun exist() = object : Matcher<File> {
  override fun test(value: File) = Result(value.exists(), "File $value should exist", "File $value should not exist")
}

fun haveExtension(vararg exts: String) = object : Matcher<File> {
  override fun test(value: File) = Result(exts.any { value.name.endsWith(it) }, "File $value should end with one of $exts", "File $value should not end with one of $exts")
}

fun aDirectory(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.isDirectory, "File $value should be a directory", "File $value should not be a directory")
}

fun aFile(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.isFile, "File $value should be a file", "File $value should not be a file")
}

fun beAbsolute(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.isAbsolute, "File $value should be absolute", "File $value should not be absolute")
}

fun beRelative(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(!value.isAbsolute, "File $value should be relative", "File $value should not be relative")
}

fun beReadable(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.canRead(), "File $value should be readable", "File $value should not be readable")
}

fun beWriteable(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.canWrite(), "File $value should be writeable", "File $value should not be writeable")
}

fun beExecutable(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.canExecute(), "File $value should be executable", "File $value should not be executable")
}

fun beHidden(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.isHidden, "File $value should be hidden", "File $value should not be hidden")
}

fun startWithPath(path: Path) = startWithPath(path.toFile())
fun startWithPath(file: File) = startWithPath(file.toString())
fun startWithPath(prefix: String) = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.toString().startsWith(prefix), "File $value should start with $prefix", "File $value should not start with $prefix")
}

