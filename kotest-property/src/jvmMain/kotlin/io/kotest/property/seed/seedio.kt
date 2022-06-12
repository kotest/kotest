package io.kotest.property.seed

import io.kotest.assertions.print.print
import io.kotest.common.TestPath
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.notExists

actual fun readSeed(path: TestPath): Long? {
   return try {
      val p = seedPath(path)
      if (p.notExists()) {
         null
      } else {
         Files.readAllLines(p).firstOrNull()?.trim()?.toLongOrNull()
      }
   } catch (e: Exception) {
      println("Error reading seed")
      e.print()
      null
   }

fun seedDirectory(): Path = Paths.get(System.getProperty("user.home")).resolve(".kotest").resolve("seeds")

fun seedPath(path: TestPath): Path {
   return seedDirectory().resolve(path.value)
}

actual fun writeSeed(path: TestPath, seed: Long) {
   try {
      val f = seedPath(path)
      f.parent.toFile().mkdirs()
      Files.write(f, seed.toString().encodeToByteArray())
   } catch (e: Exception) {
      println("Error writing seed")
      e.print()
   }
}

actual fun clearSeed(path: TestPath) {
   try {
      val f = seedPath(path)
      f.toFile().deleteRecursively()
   } catch (e: Exception) {
      println("Error clearing seed")
      e.print()
   }
}
