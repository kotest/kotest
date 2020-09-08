package io.kotest.property.arbitrary

import io.kotest.property.Arb
import java.io.File
import java.nio.charset.Charset
import kotlin.random.Random

/**
 * Returns an [Arb] where each value is a randomly created File object.
 * The file objects do not necessarily exist on disk.
 */
fun Arb.Companion.file(): Arb<File> = Arb.string(1..100).map { File(it) }

/**
 * Returns an [Arb] where each value is a randomly chosen File object from given directory.
 * If the Directory does not exist or is empty, this function will throw an error.
 *
 * If recursive is true(default value is false) it gives files from inner directories as well recursively.
 */
fun Arb.Companion.file(directoryName: String, recursive: Boolean = false): Arb<File> {

   fun randomFile(files: Sequence<File>, random: Random): File {
      val allFiles = files.toList()
      if (allFiles.isEmpty()) error("No files to enumerate")
      return allFiles.shuffled(random).first()
   }

   return arbitrary {
      val fileTreeWalk = File(directoryName).walk()
      when (recursive) {
         true -> randomFile(fileTreeWalk.maxDepth(Int.MAX_VALUE), it.random)
         else -> randomFile(fileTreeWalk.maxDepth(1), it.random)
      }
   }
}

/**
 * Returns an [Arb] where each value is a randomly chosen line in the given file.
 */
fun Arb.Companion.lines(file: File, charset: Charset = Charsets.UTF_8): Arb<String> {
   val contents = file.readLines(charset)
   return arbitrary {
      contents.shuffled(it.random).first()
   }
}
