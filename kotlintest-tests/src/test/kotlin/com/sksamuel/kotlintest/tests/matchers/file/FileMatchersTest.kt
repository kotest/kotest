package com.sksamuel.kotlintest.tests.matchers.file

import io.kotlintest.matchers.file.aDirectory
import io.kotlintest.matchers.file.aFile
import io.kotlintest.matchers.file.absolute
import io.kotlintest.matchers.file.exist
import io.kotlintest.matchers.file.haveExtension
import io.kotlintest.matchers.file.relative
import io.kotlintest.matchers.should
import io.kotlintest.specs.FunSpec
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import java.io.File
import java.nio.file.Files

class FileMatchersTest : FunSpec() {
  init {

    test("isRelative() should match only relative files") {
      File("sammy/boy") shouldBe relative()
    }

    test("isAbsolute() should match only absolute files") {
      File("sammy/boy") shouldBe absolute()
    }

    test("exist() file matcher") {
      val file = Files.createTempFile("test", "test").toFile()
      file should exist()

      shouldThrow<AssertionError> {
        File("qweqwewqewqewee") should exist()
      }

      file.delete()
    }

    test("haveExtension") {
      val file = Files.createTempFile("test", ".test").toFile()
      file should haveExtension(".test")

      shouldThrow<AssertionError> {
        file should haveExtension(".jpeg")
      }

      file.delete()
    }

    test("aFile() file matcher") {
      val file = Files.createTempFile("test", "test").toFile()
      file shouldBe aFile()

      shouldThrow<AssertionError> {
        file shouldBe aDirectory()
      }

      file.delete()
    }

    test("aDirectory() file matcher") {
      val dir = Files.createTempDirectory("testdir").toFile()
      dir shouldBe aDirectory()

      shouldThrow<AssertionError> {
        dir shouldBe aFile()
      }
    }
  }
}