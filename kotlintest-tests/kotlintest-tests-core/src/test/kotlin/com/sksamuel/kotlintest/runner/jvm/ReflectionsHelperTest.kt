package com.sksamuel.kotlintest.runner.jvm

import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.runner.jvm.ReflectionsHelper
import io.kotlintest.specs.WordSpec
import java.nio.file.Files

class ReflectionsHelperTest : WordSpec({

  "IgnoreEmptyDirectoryUrlType" should {
    "match directory with no contents" {
      val dir = Files.createTempDirectory("wibble")
      ReflectionsHelper.IgnoreEmptyDirectoryUrlType.matches(dir.toUri().toURL()).shouldBeTrue()
    }
    "not match directory with contents" {
      val dir = Files.createTempDirectory("wibble")
      dir.resolve("foo").toFile().createNewFile()
      ReflectionsHelper.IgnoreEmptyDirectoryUrlType.matches(dir.toUri().toURL()).shouldBeFalse()
    }
  }

  "EmptyIfFileEndingsUrlType" should {
    "match files which have a given extension" {
      val file1 = Files.createTempFile("wibble", ".foo")
      val file2 = Files.createTempFile("wibble", ".bar")
      val vfs = ReflectionsHelper.EmptyIfFileEndingsUrlType(listOf("foo"))
      vfs.matches(file1.toUri().toURL()).shouldBeTrue()
      vfs.matches(file2.toUri().toURL()).shouldBeFalse()
    }
  }
})