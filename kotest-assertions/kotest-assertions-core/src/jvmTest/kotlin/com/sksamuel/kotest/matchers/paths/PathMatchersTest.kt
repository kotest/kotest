package com.sksamuel.kotest.matchers.paths

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.paths.shouldBeEmptyDirectory
import io.kotest.matchers.paths.shouldBeNonEmptyDirectory
import io.kotest.matchers.paths.shouldNotBeEmptyDirectory
import io.kotest.matchers.paths.shouldNotBeNonEmptyDirectory
import java.nio.file.Files

@Suppress("BlockingMethodInNonBlockingContext")
class PathMatchersTest : FunSpec() {

  init {

    test("directory should be empty (deprecated)") {
       val path = Files.createTempDirectory("testdir")
       path.shouldNotBeNonEmptyDirectory()
       Files.write(path.resolve("testfile.txt"), byteArrayOf(1, 2, 3))
       path.shouldBeNonEmptyDirectory()
    }

    test("directory should be empty") {
      val path = Files.createTempDirectory("testdir")
      path.shouldBeEmptyDirectory()
      Files.write(path.resolve("testfile.txt"), byteArrayOf(1, 2, 3))
      path.shouldNotBeEmptyDirectory()
    }
  }
}
