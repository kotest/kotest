package com.sksamuel.kotest.matchers.paths

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.paths.shouldBeAFile
import io.kotest.matchers.paths.shouldBeEmptyDirectory
import io.kotest.matchers.paths.shouldContainNFiles
import io.kotest.matchers.paths.shouldNotBeEmptyDirectory
import io.kotest.matchers.paths.shouldNotContainNFiles
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

   context("containNFiles with non-standard filesystem") {
      test("should work with Jimfs in-memory filesystem") {
         val fs = Jimfs.newFileSystem(Configuration.unix())
         val dir = fs.getPath("/testdir")
         Files.createDirectory(dir)

         // Initially empty directory should contain 0 files
         dir.shouldContainNFiles(0)
         dir.shouldNotContainNFiles(1)

         // Add one file
         Files.createFile(dir.resolve("file1.txt"))
         dir.shouldContainNFiles(1)
         dir.shouldNotContainNFiles(0)
         dir.shouldNotContainNFiles(2)

         // Add two more files
         Files.createFile(dir.resolve("file2.txt"))
         Files.createFile(dir.resolve("file3.txt"))
         dir.shouldContainNFiles(3)
         dir.shouldNotContainNFiles(2)

         // Test that it fails for wrong count
         shouldThrow<AssertionError> {
            dir.shouldContainNFiles(5)
         }

         fs.close()
      }

      test("should count files and directories separately with Jimfs") {
         val fs = Jimfs.newFileSystem(Configuration.unix())
         val dir = fs.getPath("/testdir")
         Files.createDirectory(dir)

         // Add files and subdirectories
         Files.createFile(dir.resolve("file1.txt"))
         Files.createFile(dir.resolve("file2.txt"))
         Files.createDirectory(dir.resolve("subdir1"))
         Files.createDirectory(dir.resolve("subdir2"))

         // Should count both files and directories
         dir.shouldContainNFiles(4)

         fs.close()
      }
   }
})
