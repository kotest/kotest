package io.kotlintest.matchers.file

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe
import java.io.File
import java.nio.file.Path

fun File.shouldBeEmpty() = this shouldBe emptyFile()
fun File.shouldNotBeEmpty() = this shouldNotBe emptyFile()
fun emptyFile(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.length() == 0L, "File $value should be empty", "File $value should not be empty")
}

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

fun File.shouldHavePath(name: String) = this should havePath(name)
fun File.shouldNotHavePath(name: String) = this shouldNot havePath(name)
fun havePath(name: String) = object : Matcher<File> {
  override fun test(value: File) = Result(value.path == name, "File $value should have path $name", "File $value should not have path $name")
}

fun File.shouldHaveName(name: String) = this should haveName(name)
fun File.shouldNotHaveName(name: String) = this shouldNot haveName(name)
fun haveName(name: String) = object : Matcher<File> {
  override fun test(value: File) = Result(value.name == name, "File $value should have name $name", "File $value should not have name $name")
}

fun Path.shouldContainFile(name: String) = this.toFile() should containFile(name)
fun Path.shouldNotContainFile(name: String) = this.toFile() shouldNot containFile(name)

fun File.shouldContainFile(name: String) = this should containFile(name)
fun File.shouldNotContainFile(name: String) = this shouldNot containFile(name)
fun containFile(name: String) = object : Matcher<File> {
  override fun test(value: File): Result {
    val contents = value.list()
    val passed = value.isDirectory && contents.contains(name)
    return Result(passed,
        "Directory $value should contain a file with filename $name (detected ${contents.size} other files)",
        "Directory $value should not contain a file with filename $name"
    )
  }
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

fun Path.shouldBeCanonical() = this.toFile() should beCanonicalPath()
fun Path.shouldNotBeCanonical() = this.toFile() shouldNot beCanonicalPath()
fun File.shouldBeCanonical() = this should beCanonicalPath()
fun File.shouldNotBeCanonical() = this shouldNot beCanonicalPath()
fun beCanonicalPath(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.canonicalPath == value.path, "File $value should be canonical", "File $value should not be canonical")
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

fun Path.shouldHaveFileSize(size: Long) = this.toFile() should haveFileSize(size)
fun Path.shouldNotHaveFileSize(size: Long) = this.toFile() shouldNot haveFileSize(size)
fun File.shouldHaveFileSize(size: Long) = this should haveFileSize(size)
fun File.shouldNotHaveFileSize(size: Long) = this shouldNot haveFileSize(size)
fun haveFileSize(size: Long): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.length() == size, "File $value should have size $size", "File $value should not have size $size")
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

fun File.shouldBeReadable() = this should beReadable()
fun File.shouldNotBeReadable() = this shouldNot beReadable()
fun beReadable(): Matcher<File> = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.canRead(), "File $value should be readable", "File $value should not be readable")
}

fun File.shouldStartWithPath(path: Path) = this should startWithPath(path)
fun File.shouldNotStartWithPath(path: Path) = this shouldNot startWithPath(path)

fun File.shouldStartWithPath(prefix: String) = this should startWithPath(prefix)
fun File.shouldNotStartWithPath(prefix: String) = this shouldNot startWithPath(prefix)

fun File.shouldStartWithPath(file: File) = this should startWithPath(file)
fun File.shouldNotStartWithPath(file: File) = this shouldNot startWithPath(file)

fun Path.shouldStartWithPath(path: Path) = this.toFile() should startWithPath(path)
fun Path.shouldNotStartWithPath(path: Path) = this.toFile() shouldNot startWithPath(path)

fun startWithPath(path: Path) = startWithPath(path.toFile())
fun startWithPath(file: File) = startWithPath(file.toString())
fun startWithPath(prefix: String) = object : Matcher<File> {
  override fun test(value: File): Result = Result(value.toString().startsWith(prefix), "File $value should start with $prefix", "File $value should not start with $prefix")
}

fun Path.shouldExist() = this.toFile() should exist()
fun Path.shouldNotExist() = this.toFile() shouldNot exist()

fun Path.shouldHaveExtension(vararg exts: String) = this.toFile() should haveExtension(*exts)
fun Path.shouldNotHaveExtension(vararg exts: String) = this.toFile() shouldNot haveExtension(*exts)

fun Path.shouldBeADirectory() = this.toFile() should aDirectory()
fun Path.shouldNotBeADirectory() = this.toFile() shouldNot aDirectory()

fun Path.shouldBeAFile() = this.toFile() should aFile()
fun Path.shouldNotBeAFile() = this.toFile() shouldNot aFile()

fun Path.shouldBeAbsolute() = this.toFile() should beAbsolute()
fun Path.shouldNotBeAbsolute() = this.toFile() shouldNot beAbsolute()

fun Path.shouldBeRelative() = this.toFile() should beRelative()
fun Path.shouldNotBeRelative() = this.toFile() shouldNot beRelative()

fun Path.shouldBeReadable() = this.toFile() should beReadable()
fun Path.shouldNotBeReadable() = this.toFile() shouldNot beReadable()

fun Path.shouldBeWriteable() = this.toFile() should beWriteable()
fun Path.shouldNotBeWriteable() = this.toFile() shouldNot beWriteable()

fun Path.shouldBeExecutable() = this.toFile() should beExecutable()
fun Path.shouldNotBeExecutable() = this.toFile() shouldNot beExecutable()

fun Path.shouldBeHidden() = this.toFile() should beHidden()
fun Path.shouldNotBeHidden() = this.toFile() shouldNot beHidden()

fun Path.shouldStartWithPath(file: File) = this.toFile() should startWithPath(file)
fun Path.shouldNotStartWithPath(file: File) = this.toFile() shouldNot startWithPath(file)

fun Path.shouldStartWithPath(prefix: String) = this.toFile() should startWithPath(prefix)
fun Path.shouldNotStartWithPath(prefix: String) = this.toFile() shouldNot startWithPath(prefix)

