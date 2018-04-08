package com.sksamuel.kotlintest.tests.matchers.file

import io.kotlintest.matchers.file.aDirectory
import io.kotlintest.matchers.file.aFile
import io.kotlintest.matchers.file.beAbsolute
import io.kotlintest.matchers.file.exist
import io.kotlintest.matchers.file.haveExtension
import io.kotlintest.matchers.file.beRelative
import io.kotlintest.matchers.file.startWithPath
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FunSpec
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class FileMatchersTest : FunSpec() {
  init {

    test("relative() should match only relative files") {
      File("sammy/boy") shouldBe beRelative()
    }

    test("absolute() should match only absolute files") {
      File("/sammy/boy") shouldBe beAbsolute()
    }

    test("startWithPath() should only match files that start with the given path") {
      File("sammy/boy") should startWithPath("sammy")
      File("sammy/boy") should startWithPath(Paths.get("sammy"))
      File("/sammy/boy") should startWithPath("/sammy")
      File("/sammy/boy") should startWithPath(Paths.get("/sammy"))
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