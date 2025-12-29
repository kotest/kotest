package io.kotest.matchers.paths

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name

fun Path.shouldContainFiles(vararg files: String) = this should containFiles(files.asList())
fun Path.shouldNotContainFiles(vararg files: String) = this shouldNot containFiles(files.asList())

fun containFiles(names: List<String>) = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult {

      val contents = Files.newDirectoryStream(value).toList().map { it.name }
      val existingFiles = names.intersect(contents.toSet())
      val nonExistingFiles = names.subtract(existingFiles)

      return MatcherResult(
         nonExistingFiles.isEmpty(),
         { buildMessage(value, nonExistingFiles, false) },
         { buildMessage(value, existingFiles, true) }
      )
   }

   private fun buildMessage(path: Path, fileList: Set<String>, isNegative: Boolean): String {
      val fileString = if (fileList.size > 1) "Files" else "File"
      val negativeWord = if (isNegative) " not" else ""
      val filesString = fileList.sorted().joinToString(", ")
      return "$fileString $filesString should$negativeWord exist in $path"
   }
}

infix fun Path.shouldContainFile(name: String) = this should containFile(name)
infix fun Path.shouldNotContainFile(name: String) = this shouldNot containFile(name)

fun containFile(name: String) = object : Matcher<Path> {
   override fun test(value: Path): MatcherResult {
      val contents = Files.newDirectoryStream(value).toList().map { it.name }
      val passed = Files.isDirectory(value) && contents.contains(name)
      return MatcherResult(
         passed,
         { "Directory $value should contain a file with filename $name (detected ${contents.size} other files)" },
         { "Directory $value should not contain a file with filename $name" })
   }
}
