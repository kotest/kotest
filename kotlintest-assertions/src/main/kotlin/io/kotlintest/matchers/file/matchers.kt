package io.kotlintest.matchers.file

import io.kotlintest.Matcher
import io.kotlintest.Result
import java.io.File

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

fun absolute(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.isAbsolute, "File $value should be absolute", "File $value should not be absolute")
}

fun relative(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(!value.isAbsolute, "File $value should be relative", "File $value should not be relative")
}