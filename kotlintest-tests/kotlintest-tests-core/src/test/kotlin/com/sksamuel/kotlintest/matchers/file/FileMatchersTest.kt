package com.sksamuel.kotlintest.matchers.file

import io.kotlintest.matchers.file.*
import io.kotlintest.matchers.string.shouldEndWith
import io.kotlintest.matchers.string.shouldMatch
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FunSpec
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@Suppress("BlockingMethodInNonBlockingContext")
class FileMatchersTest : FunSpec() {
  private val osName = System.getProperty("os.name").toLowerCase()
  private val isWindows = osName.contains("windows")

  init {

    test("relative() should match only relative files") {
      File("sammy/boy") shouldBe beRelative()
      File("sammy/boy").shouldBeRelative()
    }

    test("absolute() should match only absolute files") {
      val root = if (isWindows) "C:/" else "/"
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
      }.message shouldBe "File ${dir.resolve("b")} (3 bytes) should be smaller than ${dir.resolve("a")} (2 bytes)"
    }

    test("beLarger should compare file sizes") {
      val dir = Files.createTempDirectory("testdir")
      Files.write(dir.resolve("a"), byteArrayOf(1, 2, 3))
      Files.write(dir.resolve("b"), byteArrayOf(1, 2))
      dir.resolve("a").shouldBeLarger(dir.resolve("b"))
      shouldThrow<AssertionError> {
        dir.resolve("b").shouldBeLarger(dir.resolve("a"))
      }.message shouldBe "File ${dir.resolve("b")} (2 bytes) should be larger than ${dir.resolve("a")} (3 bytes)"
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
      }.message shouldBe "File $nonExistentFileName should exist in $rootDir"

      shouldThrow<AssertionError> {
        rootDir.shouldNotContainFileDeep(rootFileName)
      }.message shouldBe "File $rootFileName should not exist in $rootDir"
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
  }
}
