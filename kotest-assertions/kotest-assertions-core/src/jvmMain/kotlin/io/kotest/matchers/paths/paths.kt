package io.kotest.matchers.paths

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.file.beLarger
import io.kotest.matchers.file.beEmptyDirectory
import io.kotest.matchers.file.containNFiles
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.isRegularFile

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

infix fun Path.shouldContainNFiles(n: Int) = this.toFile() shouldBe containNFiles(n)
infix fun Path.shouldNotContainNFiles(n: Int) = this.toFile() shouldNotBe containNFiles(n)

@Deprecated(
   message = "checks if a directory is empty. Deprecated since 4.3.",
   replaceWith = ReplaceWith("shouldBeEmptyDirectory()")
)
fun Path.shouldBeNonEmptyDirectory() = this.toFile() shouldNot beEmptyDirectory()

@Deprecated(
   message = "checks if a directory is not empty. Deprecated since 4.3.",
   replaceWith = ReplaceWith("shouldBeNonEmptyDirectory()")
)
fun Path.shouldNotBeNonEmptyDirectory() = this.toFile() should beEmptyDirectory()

fun Path.shouldBeEmptyDirectory() = this.toFile() should beEmptyDirectory()
fun Path.shouldNotBeEmptyDirectory() = this.toFile() shouldNot beEmptyDirectory()

fun Path.shouldBeHidden() = this should beHidden()
fun Path.shouldNotBeHidden() = this shouldNot beHidden()
fun beHidden(): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult =
      MatcherResult(
         Files.isHidden(value),
         { "Path $value should be hidden" },
         { "Path $value should not be hidden" })
}

fun Path.shouldBeCanonical() = this should beCanonicalPath()
fun Path.shouldNotBeCanonical() = this shouldNot beCanonicalPath()
fun beCanonicalPath(): Matcher<Path> = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult = MatcherResult(
      value.toFile().canonicalPath == value.toFile().path,
      { "File $value should be canonical" },
      { "File $value should not be canonical" })
}

infix fun Path.shouldContainFile(name: String) = this should containFile(name)
infix fun Path.shouldNotContainFile(name: String) = this shouldNot containFile(name)
fun containFile(name: String) = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult {
      val contents = value.toFile().list()
      val passed = Files.isDirectory(value) && contents.contains(name)
      return MatcherResult(
         passed,
         { "Directory $value should contain a file with filename $name (detected ${contents.size} other files)" },
         { "Directory $value should not contain a file with filename $name" })
   }
}

infix fun Path.shouldBeLarger(other: Path) = this.toFile() should beLarger(other.toFile())
infix fun Path.shouldBeLarger(other: File) = this.toFile() should beLarger(other)
infix fun Path.shouldNotBeLarger(other: Path) = this.toFile() shouldNot beLarger(other.toFile())
infix fun Path.shouldNotBeLarger(other: File) = this.toFile() shouldNot beLarger(other)
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

infix fun Path.shouldContainFileDeep(name: String) = this should containFileDeep(name)
infix fun Path.shouldNotContainFileDeep(name: String) = this shouldNot containFileDeep(name)
fun containFileDeep(name: String): Matcher<Path> = object : Matcher<Path> {

   private fun fileExists(dir: Path): Boolean {
      val contents = dir.toFile().listFiles()
      val (dirs, files) = contents.partition { it.isDirectory }
      return files.map { it.name.toString() }.contains(name) || dirs.any { fileExists(it.toPath()) }
   }

   override fun test(value: Path): MatcherResult = MatcherResult(
      fileExists(value),
      { "Path $name should exist in $value" },
      { "Path $name should not exist in $value" }
   )
}

fun Path.shouldContainFiles(vararg files: String) = this should containFiles(files.asList())
fun Path.shouldNotContainFiles(vararg files: String) = this shouldNot containFiles(files.asList())
fun containFiles(names: List<String>) = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult {

      val files = Files.list(value).toList().map { it.fileName.toString() }

      val existingFiles = names.intersect(files)
      val nonExistingFiles = names.subtract(existingFiles)

      return MatcherResult(
         nonExistingFiles.isEmpty(),
         { buildMessage(value, nonExistingFiles, false) },
         {
            buildMessage(value, existingFiles, true)
         })
   }

   private fun buildMessage(path: Path, fileList: Set<String>, isNegative: Boolean): String {
      val fileString = if (fileList.size > 1) "Files" else "File"
      val negativeWord = if (isNegative) " not" else ""
      val filesString = fileList.sorted().joinToString(", ")
      return "$fileString $filesString should$negativeWord exist in $path"
   }
}

fun Path.shouldBeSymbolicLink() = this should beSymbolicLink()
fun Path.shouldNotBeSymbolicLink() = this shouldNot beSymbolicLink()
fun beSymbolicLink() = object : Matcher<Path> {
   override fun test(value: Path) = MatcherResult(
      Files.isSymbolicLink(value),
      { "Path $value should be a symbolic link" },
      { "Path $value should not be a symbolic link" }
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
