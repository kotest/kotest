package com.sksamuel.kotest.matchers.file

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.file.aDirectory
import io.kotest.matchers.file.aFile
import io.kotest.matchers.file.beAbsolute
import io.kotest.matchers.file.beRelative
import io.kotest.matchers.file.exist
import io.kotest.matchers.file.haveExtension
import io.kotest.matchers.file.shouldBeADirectory
import io.kotest.matchers.file.shouldBeAFile
import io.kotest.matchers.file.shouldBeAbsolute
import io.kotest.matchers.file.shouldBeEmptyDirectory
import io.kotest.matchers.file.shouldBeRelative
import io.kotest.matchers.file.shouldBeSymbolicLink
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.file.shouldHaveExtension
import io.kotest.matchers.file.shouldHaveParent
import io.kotest.matchers.file.shouldHaveSameStructureAndContentAs
import io.kotest.matchers.file.shouldHaveSameStructureAs
import io.kotest.matchers.file.shouldNotBeADirectory
import io.kotest.matchers.file.shouldNotBeAFile
import io.kotest.matchers.file.shouldNotBeEmptyDirectory
import io.kotest.matchers.file.shouldNotBeSymbolicLink
import io.kotest.matchers.file.shouldNotExist
import io.kotest.matchers.file.shouldNotHaveExtension
import io.kotest.matchers.file.shouldNotHaveParent
import io.kotest.matchers.file.shouldStartWithPath
import io.kotest.matchers.file.startWithPath
import io.kotest.matchers.paths.shouldBeLarger
import io.kotest.matchers.paths.shouldBeSmaller
import io.kotest.matchers.paths.shouldBeSymbolicLink
import io.kotest.matchers.paths.shouldContainFile
import io.kotest.matchers.paths.shouldContainFileDeep
import io.kotest.matchers.paths.shouldContainFiles
import io.kotest.matchers.paths.shouldHaveParent
import io.kotest.matchers.paths.shouldNotBeSymbolicLink
import io.kotest.matchers.paths.shouldNotContainFile
import io.kotest.matchers.paths.shouldNotContainFileDeep
import io.kotest.matchers.paths.shouldNotContainFiles
import io.kotest.matchers.paths.shouldNotHaveParent
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldMatch
import org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.writeBytes

@Suppress("BlockingMethodInNonBlockingContext")
class FileMatchersTest : FunSpec() {

   init {

      test("relative() should match only relative files") {
         File("sammy/boy") shouldBe beRelative()
         File("sammy/boy").shouldBeRelative()
      }

      test("absolute() should match only absolute files") {
         val root = if (IS_OS_WINDOWS) "C:/" else "/"
         File("${root}sammy/boy") shouldBe beAbsolute()
         File("${root}sammy/boy").shouldBeAbsolute()
      }

      test("startWithPath() should only match files that start with the given path") {
         File("sammy/boy") should startWithPath("sammy")
         File("sammy/boy") should startWithPath(Paths.get("sammy"))
         File("/sammy/boy") should startWithPath("${File.separator}sammy")
         File("/sammy/boy") should startWithPath(Paths.get("/sammy"))

         File("/sammy/boy").shouldStartWithPath("${File.separator}sammy")
         File("/sammy/boy").shouldStartWithPath(Paths.get("/sammy"))
      }

      test("exist() file matcher") {
         val file = Files.createTempFile("test", "test").toFile()
         file should exist()

         shouldThrow<AssertionError> {
            File("qweqwewqewqewee") should exist()
         }.message shouldBe "File qweqwewqewqewee should exist"

         file.shouldExist()

         shouldThrow<AssertionError> {
            file.shouldNotExist()
         }

         file.delete()
      }

      test("haveExtension") {
         val file = Files.createTempFile("test", ".test").toFile()
         file should haveExtension(".test")
         file shouldNot haveExtension(".wibble")

         shouldThrow<AssertionError> {
            file should haveExtension(".jpeg")
         }.message.shouldEndWith("with one of .jpeg")

         shouldThrow<AssertionError> {
            file.shouldHaveExtension(".jpeg")
         }.message.shouldEndWith("with one of .jpeg")

         file.shouldHaveExtension(".test")
         file.shouldNotHaveExtension(".wibble")

         file.delete()
      }

      test("aFile() file matcher") {
         val file = Files.createTempFile("test", "test").toFile()
         file shouldBe aFile()
         file.shouldBeAFile()

         shouldThrow<AssertionError> {
            file shouldBe aDirectory()
         }

         shouldThrow<AssertionError> {
            file.shouldNotBeAFile()
         }

         shouldThrow<AssertionError> {
            file.shouldBeADirectory()
         }

         file.delete()
      }

      test("aDirectory() file matcher") {
         val dir = Files.createTempDirectory("testdir").toFile()
         dir shouldBe aDirectory()
         dir.shouldBeADirectory()

         shouldThrow<AssertionError> {
            dir shouldBe aFile()
         }

         shouldThrow<AssertionError> {
            dir.shouldNotBeADirectory()
         }

         shouldThrow<AssertionError> {
            dir shouldBe aFile()
         }
      }

      test("shouldBeEmptyDirectory") {

         val dir = Files.createTempDirectory("testdir").toFile()
         dir.shouldBeEmptyDirectory() // is empty

         shouldThrowAny {
            dir.resolve("testfile.txt").writeBytes(byteArrayOf(1, 2, 3))
            dir.shouldBeEmptyDirectory() // no longer empty so should throw
         }

         shouldThrowAny {
            val file = Files.createTempFile("foo", "bar")
            file.toFile().shouldBeEmptyDirectory() // empty file so should throw
         }

         shouldThrowAny {
            val file = Files.createTempFile("foo", "bar")
            file.writeBytes(byteArrayOf(1, 2, 3))
            file.toFile().shouldBeEmptyDirectory() // non empty file so should throw
         }
      }

      test("shouldNotBeEmptyDirectory") {

         val dir = Files.createTempDirectory("testdir").toFile()
         shouldThrowAny {
            dir.shouldNotBeEmptyDirectory() // this is empty
         }

         dir.resolve("testfile.txt").writeBytes(byteArrayOf(1, 2, 3))
         dir.shouldNotBeEmptyDirectory() // no longer empty

         val file1 = Files.createTempFile("foo", "bar")
         file1.toFile().shouldNotBeEmptyDirectory() // empty file

         val file2 = Files.createTempFile("foo", "bar")
         file2.writeBytes(byteArrayOf(1, 2, 3))
         file2.toFile().shouldNotBeEmptyDirectory() // non empty file
      }

      test("directory contains file matching predicate") {
         val dir = Files.createTempDirectory("testdir")
         dir.resolve("a").toFile().createNewFile()
         dir.resolve("b").toFile().createNewFile()
         dir.shouldContainFile("a")
         dir.shouldNotContainFile("c")
         shouldThrow<AssertionError> {
            dir.shouldContainFile("c")
         }.message?.shouldMatch("^Directory .+ should contain a file with filename c \\(detected 2 other files\\)$".toRegex())
      }

      test("beSmaller should compare file sizes") {
         val dir = Files.createTempDirectory("testdir")
         Files.write(dir.resolve("a"), byteArrayOf(1, 2))
         Files.write(dir.resolve("b"), byteArrayOf(1, 2, 3))
         dir.resolve("a").shouldBeSmaller(dir.resolve("b"))
         shouldThrow<AssertionError> {
            dir.resolve("b").shouldBeSmaller(dir.resolve("a"))
         }.message shouldBe "Path ${dir.resolve("b")} (3 bytes) should be smaller than ${dir.resolve("a")} (2 bytes)"
      }

      test("containsFileDeep should find file deep") {
         val rootFileName = "super_dooper_hyper_file_root"
         val innerFileName = "super_dooper_hyper_file_inner"
         val nonExistentFileName = "super_dooper_hyper_non_existent_file"

         val rootDir = Files.createTempDirectory("testdir")
         val innerDir = Files.createDirectories(rootDir.resolve("innerfolder"))

         Files.write(rootDir.resolve(rootFileName), byteArrayOf(1, 2, 3))
         Files.write(innerDir.resolve(innerFileName), byteArrayOf(1, 2, 3))

         rootDir.shouldContainFileDeep(rootFileName)
         rootDir.shouldContainFileDeep(innerFileName)

         shouldThrow<AssertionError> {
            rootDir.shouldContainFileDeep(nonExistentFileName)
         }.message shouldBe "Path $nonExistentFileName should exist in $rootDir"

         shouldThrow<AssertionError> {
            rootDir.shouldNotContainFileDeep(rootFileName)
         }.message shouldBe "Path $rootFileName should not exist in $rootDir"
      }

      test("shouldContainFiles should check if files exists") {
         val testDir = Files.createTempDirectory("testdir")

         Files.write(testDir.resolve("a.txt"), byteArrayOf(1, 2, 3))
         Files.write(testDir.resolve("b.gif"), byteArrayOf(1, 2, 3))
         Files.write(testDir.resolve("c.doc"), byteArrayOf(1, 2, 3))

         testDir.shouldContainFiles("a.txt", "b.gif", "c.doc")
         testDir.shouldNotContainFiles("d.txt", "e.gif", "f.doc")

         shouldThrow<AssertionError> {
            testDir.shouldContainFiles("d.txt")
         }.message shouldBe "File d.txt should exist in $testDir"

         shouldThrow<AssertionError> {
            testDir.shouldContainFiles("d.txt", "e.gif")
         }.message shouldBe "Files d.txt, e.gif should exist in $testDir"

         shouldThrow<AssertionError> {
            testDir.shouldNotContainFiles("a.txt")
         }.message shouldBe "File a.txt should not exist in $testDir"

         shouldThrow<AssertionError> {
            testDir.shouldNotContainFiles("a.txt", "b.gif")
         }.message shouldBe "Files a.txt, b.gif should not exist in $testDir"
      }

      test("shouldBeSymbolicLink should check if file is symbolic link").config(enabled = isNotWindowsOrIsWindowsElevated()) {
         val testDir = Files.createTempDirectory("testdir")

         val existingFile = Files.write(testDir.resolve("original.txt"), byteArrayOf(1, 2, 3, 4))
         val existingFileAsFile = existingFile.toFile()
         val link = Files.createSymbolicLink(testDir.resolve("a.txt"), existingFile)
         val linkAsFile = link.toFile()

         link.shouldBeSymbolicLink()
         linkAsFile.shouldBeSymbolicLink()

         existingFile.shouldNotBeSymbolicLink()
         existingFileAsFile.shouldNotBeSymbolicLink()
      }

      test("shouldHaveParent should check if file has any parent with given name") {
         val testDir = Files.createTempDirectory("testdir")

         val subdir = Files.createDirectory(testDir.resolve("sub_testdir"))
         val file = Files.write(subdir.resolve("a.txt"), byteArrayOf(1, 2, 3, 4))
         val fileAsFile = file.toFile()

         file.shouldHaveParent(testDir.toFile().name)
         file.shouldHaveParent(subdir.toFile().name)
         file.shouldNotHaveParent("super_hyper_long_random_file_name")

         fileAsFile.shouldHaveParent(testDir.toFile().name)
         fileAsFile.shouldHaveParent(subdir.toFile().name)
         fileAsFile.shouldNotHaveParent("super_hyper_long_random_file_name")
      }

      test("shouldHaveSameStructureAs and shouldHaveSameStructureAndContentAs two file trees") {
         val testDir = Files.createTempDirectory("testdir")

         val expectDir = File("$testDir/expect").apply {
            File("$this/a.txt").createWithContent(byteArrayOf(1, 2, 3))
            File("$this/b.txt").createWithContent(byteArrayOf(1, 2, 3, 4))
            File("$this/subfolder/b.txt").createWithContent(byteArrayOf(1, 2, 3, 4))
            File("$this/subfolder/subfolder-two/c.txt").createWithContent(byteArrayOf(1, 2))
         }

         val actualDir = File("$testDir/actual").apply {
            File("$this/a.txt").createWithContent(byteArrayOf(1, 2, 3))
            File("$this/b.txt").createWithContent(byteArrayOf(1, 2, 3, 4))
            File("$this/subfolder/b.txt").createWithContent(byteArrayOf(1, 2, 3, 4))
            File("$this/subfolder/subfolder-two/c.txt").createWithContent(byteArrayOf(1, 2))
         }

         expectDir shouldHaveSameStructureAs actualDir
         expectDir shouldHaveSameStructureAndContentAs actualDir

         File("$expectDir/z.txt").createWithContent(byteArrayOf(1, 2, 3))

         shouldThrow<AssertionError> { expectDir shouldHaveSameStructureAs actualDir }
         shouldThrow<AssertionError> { expectDir shouldHaveSameStructureAndContentAs actualDir }

         File("$actualDir/z.txt").createWithContent(byteArrayOf(1, 2, 3, 4))

         expectDir shouldHaveSameStructureAs actualDir
         shouldThrow<AssertionError> { expectDir shouldHaveSameStructureAndContentAs actualDir }
      }

      test("shouldHaveSameStructureAs with filter should check if two file trees are the same and files have the same content") {
         val testDir = Files.createTempDirectory("testdir")

         val expectDir = File("$testDir/expect").apply {
            File("$this/a.txt").createWithContent("a/b")
            File("$this/b.txt").createWithContent("b/c")
            File("$this/subfolder/b.txt").createWithContent("b/c")
            File("$this/subfolder/subfolder-two/c.txt").createWithContent("c/d")
            File("$this/z.txt").createWithContent("z")
         }

         val actualDir = File("$testDir/actual").apply {
            File("$this/a.txt").createWithContent("a/b")
            File("$this/b.txt").createWithContent("b/c")
            File("$this/subfolder/b.txt").createWithContent("b/c")
            File("$this/subfolder/subfolder-two/c.txt").createWithContent("c/d")
            File("$this/z.txt").createWithContent("zz")
         }

         expectDir.shouldHaveSameStructureAs(actualDir, filterLhs = { it.name == "z.txt" })

         expectDir.shouldHaveSameStructureAs(actualDir, filterRhs = { it.name == "z.txt" })
      }

      test("shouldHaveSameStructureAndContentAs with compare and filter should check if two file trees are the same and files have the same content") {
         val testDir = Files.createTempDirectory("testdir")

         val expectDir = File("$testDir/expect").apply {
            File("$this/a.txt").createWithContent("a/b")
            File("$this/b.txt").createWithContent("b/c")
            File("$this/subfolder/b.txt").createWithContent("b/c")
            File("$this/subfolder/subfolder-two/c.txt").createWithContent("c/d")
         }

         val actualDir = File("$testDir/actual").apply {
            File("$this/a.txt").createWithContent("a/b")
            File("$this/b.txt").createWithContent("b\\c")
            File("$this/subfolder/b.txt").createWithContent("b\\c")
            File("$this/subfolder/subfolder-two/c.txt").createWithContent("c\\d")
         }

         expectDir.shouldHaveSameStructureAs(actualDir) { a, b ->
            a.isFile && b.isFile && a.readText() == b.readText().replace("\\", "/")
         }

         expectDir.shouldHaveSameStructureAndContentAs(actualDir, filterLhs = { it.name != "a.txt" })
         expectDir.shouldHaveSameStructureAndContentAs(actualDir, filterRhs = { it.name != "a.txt" })
      }
   }
}

private fun File.createWithContent(content: String) {
   this.parentFile.mkdirs()
   createNewFile()
   writeText(content)
}

private fun File.createWithContent(content: ByteArray) {
   this.parentFile.mkdirs()
   createNewFile()
   writeBytes(content)
}

private fun isNotWindowsOrIsWindowsElevated(): Boolean {
   return if (!IS_OS_WINDOWS) {
      true
   } else {
      try {
         val p = Runtime.getRuntime().exec("""reg query "HKU\S-1-5-19"""")
         p.waitFor()
         0 == p.exitValue()
      } catch (ex: Exception) {
         println("Failed to determine if process had elevated permissions, assuming it does not.")
         false
      }
   }
}
