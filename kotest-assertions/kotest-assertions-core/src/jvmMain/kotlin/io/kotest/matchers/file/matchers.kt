package io.kotest.matchers.file

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.collections.shouldBeSameSizeAs
import io.kotest.matchers.paths.beSymbolicLink
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import java.io.File
import java.io.FileFilter
import java.nio.file.Path

private fun File.safeList(): List<String> = this.list()?.toList() ?: emptyList()
private fun File.safeListFiles(): List<File> = this.listFiles()?.toList() ?: emptyList()
private fun File.safeListFiles(filter: FileFilter): List<File> = this.listFiles(filter)?.toList() ?: emptyList()

fun File.shouldBeEmptyDirectory() = this should beEmptyDirectory()
fun File.shouldNotBeEmptyDirectory() = this shouldNot beEmptyDirectory()
fun beEmptyDirectory(): Matcher<File> = object : Matcher<File> {
   override fun test(value: File): MatcherResult {
      return if (value.isDirectory) {
         val contents = value.safeList()
         MatcherResult(
            contents.isEmpty(),
            { "$value should be an empty directory but contained ${contents.size} file(s) [${contents.joinToString(", ")}]" },
            { "$value should not be a non-empty directory" },
         )
      } else {
         val reason = when {
            value.isFile -> "was a regular file"
            !value.exists() -> "it does not exist"
            else -> "could not determine type"
         }
         MatcherResult(
            false,
            { "$value should be an empty directory, but $reason" },
            { "$value should not be a non-empty directory, but $reason" },
         )
      }
   }
}

infix fun File.shouldContainNFiles(n: Int) = this shouldBe containNFiles(n)
infix fun File.shouldNotContainNFiles(n: Int) = this shouldNotBe containNFiles(n)
fun containNFiles(n: Int): Matcher<File> = object : Matcher<File> {
   override fun test(value: File): MatcherResult = MatcherResult(
      value.isDirectory && value.safeList().size == n,
      { "$value should be a directory and contain $n files" },
      { "$value should not be a directory containing $n files" }
   )
}

fun File.shouldBeEmpty() = this shouldBe emptyFile()
fun File.shouldNotBeEmpty() = this shouldNotBe emptyFile()
fun emptyFile(): Matcher<File> = object : Matcher<File> {
   override fun test(value: File): MatcherResult =
      MatcherResult(
         value.length() == 0L,
         { "File $value should be empty" },
         { "File $value should not be empty" }
      )
}

fun File.shouldExist() = this should exist()
fun File.shouldNotExist() = this shouldNot exist()
fun exist() = object : Matcher<File> {
   override fun test(value: File) =
      MatcherResult(
         value.exists(),
         { "File $value should exist" },
         { "File $value should not exist" }
      )
}

infix fun File.shouldContainFile(name: String) = this should containFile(name)
infix fun File.shouldNotContainFile(name: String) = this shouldNot containFile(name)
fun containFile(name: String) = object : Matcher<File> {
   override fun test(value: File): MatcherResult {
      val contents = value.safeList()
      val passed = value.isDirectory && contents.contains(name)
      return MatcherResult(
         passed,
         { "Directory $value should contain a file with filename $name (detected ${contents.size} other files)" },
         { "Directory $value should not contain a file with filename $name" }
      )
   }
}

fun File.shouldBeSymbolicLink() = this.toPath() should beSymbolicLink()
fun File.shouldNotBeSymbolicLink() = this.toPath() shouldNot beSymbolicLink()

infix fun File.shouldHaveParent(name: String) = this should haveParent(name)
infix fun File.shouldNotHaveParent(name: String) = this shouldNot haveParent(name)
fun haveParent(name: String) = object : Matcher<File> {
   private fun isParentEqualExpected(parent: File?): Boolean =
      parent != null && (parent.name == name || isParentEqualExpected(parent.parentFile))

   override fun test(value: File) = MatcherResult(
      isParentEqualExpected(value.parentFile),
      { "File $value should have parent $name" },
      { "File $value should not have parent $name" }
   )
}

fun File.shouldBeADirectory() = this should aDirectory()
fun File.shouldNotBeADirectory() = this shouldNot aDirectory()
fun aDirectory(): Matcher<File> = object : Matcher<File> {
   override fun test(value: File): MatcherResult = MatcherResult(
      value.isDirectory,
      { "File $value should be a directory" },
      { "File $value should not be a directory" }
   )
}

fun File.shouldBeAFile() = this should aFile()
fun File.shouldNotBeAFile() = this shouldNot aFile()
fun aFile(): Matcher<File> = object : Matcher<File> {
   override fun test(value: File): MatcherResult =
      MatcherResult(
         value.isFile,
         { "File $value should be a file" },
         { "File $value should not be a file" })
}

infix fun File.shouldBeSmaller(other: Path) = this should beSmaller(other.toFile())
infix fun File.shouldBeSmaller(other: File) = this should beSmaller(other)
infix fun File.shouldNotBeSmaller(other: Path) = this shouldNot beSmaller(other.toFile())
infix fun File.shouldNotBeSmaller(other: File) = this shouldNot beSmaller(other)

fun beSmaller(other: File): Matcher<File> = object : Matcher<File> {
   override fun test(value: File): MatcherResult {
      val sizea = value.length()
      val sizeb = other.length()
      return MatcherResult(
         value.length() < other.length(),
         { "File $value ($sizea bytes) should be smaller than $other ($sizeb bytes)" },
         { "File $value ($sizea bytes) should not be smaller than $other ($sizeb bytes)" }
      )
   }
}

infix fun File.shouldBeLarger(other: Path) = this should beLarger(other.toFile())
infix fun File.shouldBeLarger(other: File) = this should beLarger(other)
infix fun File.shouldNotBeLarger(other: Path) = this shouldNot beLarger(other.toFile())
infix fun File.shouldNotBeLarger(other: File) = this shouldNot beLarger(other)

fun beLarger(other: File): Matcher<File> = object : Matcher<File> {
   override fun test(value: File): MatcherResult {
      val sizea = value.length()
      val sizeb = other.length()
      return MatcherResult(
         value.length() > other.length(),
         { "File $value ($sizea bytes) should be larger than $other ($sizeb bytes)" },
         { "File $value ($sizea bytes) should not be larger than $other ($sizeb bytes)" }
      )
   }
}

fun File.shouldBeCanonical() = this should beCanonicalPath()
fun File.shouldNotBeCanonical() = this shouldNot beCanonicalPath()
fun beCanonicalPath(): Matcher<File> = object : Matcher<File> {
   override fun test(value: File): MatcherResult = MatcherResult(
      value.canonicalPath == value.path,
      { "File $value should be canonical" },
      { "File $value should not be canonical" }
   )
}

fun File.shouldBeAbsolute() = this should beAbsolute()
fun File.shouldNotBeAbsolute() = this shouldNot beAbsolute()
fun beAbsolute(): Matcher<File> = object : Matcher<File> {
   override fun test(value: File): MatcherResult =
      MatcherResult(
         value.isAbsolute,
         { "File $value should be absolute" },
         { "File $value should not be absolute" })
}

fun File.shouldBeRelative() = this should beRelative()
fun File.shouldNotBeRelative() = this shouldNot beRelative()
fun beRelative(): Matcher<File> = object : Matcher<File> {
   override fun test(value: File): MatcherResult =
      MatcherResult(
         !value.isAbsolute,
         { "File $value should be relative" },
         { "File $value should not be relative" })
}

infix fun File.shouldHaveFileSize(size: Long) = this should haveFileSize(size)
infix fun File.shouldNotHaveFileSize(size: Long) = this shouldNot haveFileSize(size)
fun haveFileSize(size: Long): Matcher<File> = object : Matcher<File> {
   override fun test(value: File): MatcherResult = MatcherResult(
      value.length() == size,
      { "File $value should have size $size" },
      { "File $value should not have size $size" }
   )
}

fun File.shouldBeWriteable() = this should beWriteable()
fun File.shouldNotBeWriteable() = this shouldNot beWriteable()
fun beWriteable(): Matcher<File> = object : Matcher<File> {
   override fun test(value: File): MatcherResult =
      MatcherResult(
         value.canWrite(),
         { "File $value should be writeable" },
         { "File $value should not be writeable" })
}

fun File.shouldBeExecutable() = this should beExecutable()
fun File.shouldNotBeExecutable() = this shouldNot beExecutable()
fun beExecutable(): Matcher<File> = object : Matcher<File> {
   override fun test(value: File): MatcherResult = MatcherResult(
      value.canExecute(),
      { "File $value should be executable" },
      { "File $value should not be executable" }
   )
}

fun File.shouldBeHidden() = this should beHidden()
fun File.shouldNotBeHidden() = this shouldNot beHidden()
fun beHidden(): Matcher<File> = object : Matcher<File> {
   override fun test(value: File): MatcherResult =
      MatcherResult(
         value.isHidden,
         { "File $value should be hidden" },
         { "File $value should not be hidden" })
}

fun File.shouldBeReadable() = this should beReadable()
fun File.shouldNotBeReadable() = this shouldNot beReadable()
fun beReadable(): Matcher<File> = object : Matcher<File> {
   override fun test(value: File): MatcherResult =
      MatcherResult(
         value.canRead(),
         { "File $value should be readable" },
         { "File $value should not be readable" })
}

infix fun File.shouldStartWithPath(path: Path) = this should startWithPath(path)
infix fun File.shouldNotStartWithPath(path: Path) = this shouldNot startWithPath(path)

infix fun File.shouldStartWithPath(prefix: String) = this should startWithPath(prefix)
infix fun File.shouldNotStartWithPath(prefix: String) = this shouldNot startWithPath(prefix)

infix fun File.shouldStartWithPath(file: File) = this should startWithPath(file)
infix fun File.shouldNotStartWithPath(file: File) = this shouldNot startWithPath(file)

infix fun Path.shouldStartWithPath(path: Path) = this.toFile() should startWithPath(path)
infix fun Path.shouldNotStartWithPath(path: Path) = this.toFile() shouldNot startWithPath(path)

fun startWithPath(path: Path) = startWithPath(path.toFile())
fun startWithPath(file: File) = startWithPath(file.toString())
fun startWithPath(prefix: String) = object : Matcher<File> {
   override fun test(value: File): MatcherResult = MatcherResult(
      value.toString().startsWith(prefix),
      { "File $value should start with $prefix" },
      { "File $value should not start with $prefix" }
   )
}

infix fun File.shouldHaveSameStructureAs(file: File) {
   this.shouldHaveSameStructureAs(file) { _, _ -> false }
}

fun File.shouldHaveSameStructureAs(
   file: File,
   compare: (expect: File, actual: File) -> Boolean,
) {
   val expectFiles = this.walkTopDown().toList()
   val actualFiles = file.walkTopDown().toList()

   val expectParentPath = this.path
   val actualParentPath = file.path

   expectFiles shouldBeSameSizeAs actualFiles

   expectFiles.zip(actualFiles) { expect, actual ->
      when {
         compare(expect, actual) -> {}
         expect.isDirectory -> actual.shouldBeADirectory()
         expect.isFile -> {
            expect.path.removePrefix(expectParentPath)
               .shouldBe(actual.path.removePrefix(actualParentPath))
         }

         else -> error("There is an unexpected error analyzing file trees")
      }
   }
}

fun File.shouldHaveSameStructureAs(
   file: File,
   filterLhs: (File) -> Boolean = { false },
   filterRhs: (File) -> Boolean = { false },
) {
   this.shouldHaveSameStructureAs(file) { expect, actual ->
      filterLhs(expect) || filterRhs(actual)
   }
}

infix fun File.shouldHaveSameStructureAndContentAs(file: File) {
   this.shouldHaveSameStructureAndContentAs(file) { _, _ -> false }
}

fun File.shouldHaveSameStructureAndContentAs(
   file: File,
   compare: (expect: File, actual: File) -> Boolean,
) {
   val expectFiles = this.walkTopDown().toList()
   val actualFiles = file.walkTopDown().toList()

   val expectParentPath = this.path
   val actualParentPath = file.path

   expectFiles shouldBeSameSizeAs actualFiles

   expectFiles.zip(actualFiles) { expect, actual ->
      when {
         compare(expect, actual) -> {}
         expect.isDirectory -> actual.shouldBeADirectory()
         expect.isFile -> {
            expect.path.removePrefix(expectParentPath)
               .shouldBe(actual.path.removePrefix(actualParentPath))
            expect.shouldHaveSameContentAs(actual)
         }

         else -> error("There is an unexpected error analyzing file trees")
      }
   }
}

fun File.shouldHaveSameStructureAndContentAs(
   file: File,
   filterLhs: (File) -> Boolean = { false },
   filterRhs: (File) -> Boolean = { false },
) {
   this.shouldHaveSameStructureAndContentAs(file) { expect, actual ->
      filterLhs(expect) || filterRhs(actual)
   }
}
