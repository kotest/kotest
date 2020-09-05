package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
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
 * If the Directory does not exist, an empty sequence will be returned instead.
 *
 * If recursive is true(default value is false) it gives files from inner directories as well recursively.
 */
fun Arb.Companion.file(directoryName: String, recursive: Boolean = false): Arb<File> = object : Arb<File>() {

   override fun edgecases(): List<File> = emptyList()

   private fun randomiseFiles(files: Sequence<File>, random: Random): Sequence<Sample<File>> {
      val allFiles = files.toList()
      if (allFiles.isEmpty()) return emptySequence()
      return generateSequence { allFiles.shuffled(random) }.flatten().map { Sample(it) }
   }

   override fun values(rs: RandomSource): Sequence<Sample<File>> {
      val fileTreeWalk = File(directoryName).walk()
      return when (recursive) {
         true -> randomiseFiles(fileTreeWalk.maxDepth(Int.MAX_VALUE), rs.random)
         else -> randomiseFiles(fileTreeWalk.maxDepth(1), rs.random)
      }
   }
}

fun Arb.Companion.lines(file: File, charset: Charset = Charsets.UTF_8): Arb<String> {
   val contents = file.readLines(charset)
   return arb {
      contents.shuffled(it.random).asSequence()
   }
}
