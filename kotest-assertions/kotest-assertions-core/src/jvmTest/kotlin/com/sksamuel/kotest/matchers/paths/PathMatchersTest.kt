package com.sksamuel.kotest.matchers.paths

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.paths.shouldBeAFile
import io.kotest.matchers.paths.shouldBeEmptyDirectory
import io.kotest.matchers.paths.shouldNotBeEmptyDirectory
import java.nio.file.Files
import kotlin.io.path.Path

class PathMatchersTest : FunSpec({


   context("Be a file") {
      test("Should fail for files that don't exist") {
         shouldThrow<AssertionError> { Path("abc").shouldBeAFile() }
      }
   }


   test("directory should be empty (deprecated)") {
      val path = Files.createTempDirectory("testdir")
      path.shouldBeEmptyDirectory()
      Files.write(path.resolve("testfile.txt"), byteArrayOf(1, 2, 3))
      path.shouldNotBeEmptyDirectory()
   }

   test("directory should be empty") {
      val path = Files.createTempDirectory("testdir")
      path.shouldBeEmptyDirectory()
      Files.write(path.resolve("testfile.txt"), byteArrayOf(1, 2, 3))
      path.shouldNotBeEmptyDirectory()
   }
})
