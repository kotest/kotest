package com.sksamuel.kotlintest.tests.matchers

import io.kotlintest.matchers.aDirectory
import io.kotlintest.matchers.aFile
import io.kotlintest.matchers.exist
import io.kotlintest.matchers.haveExtension
import io.kotlintest.matchers.should
import io.kotlintest.specs.FunSpec
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import java.io.File
import java.nio.file.Files

class FileMatchersTest : FunSpec() {
  init {

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