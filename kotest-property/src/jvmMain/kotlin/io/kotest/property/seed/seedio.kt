package io.kotest.property.seed

import io.kotest.assertions.print.print
import io.kotest.common.DescriptorPath
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText
import kotlin.io.path.writeText

internal actual fun readSeed(path: DescriptorPath): Long? {
   return try {
      return path.seedPath()
         .takeIf(Path::exists)
         ?.readText()
         ?.trim()
         ?.toLongOrNull()
   } catch (e: Exception) {
      println("Error reading seed for $path")
      e.print()
      null
   }
}

internal actual fun writeSeed(path: DescriptorPath, seed: Long) {
   try {
      val f = path.seedPath()
      f.createParentDirectories()
      f.writeText(seed.toString())
   } catch (e: Exception) {
      println("Error writing seed $seed for $path")
      e.printStackTrace()
   }
}

internal actual fun clearSeed(path: DescriptorPath) {
   try {
      val f = path.seedPath()
      if (
         f.isRegularFile()
         && f != seedDirectory
         && f.startsWith(seedDirectory)
      ) {
         f.deleteIfExists()
      }
   } catch (e: Exception) {
      println("Error clearing seed $path")
      e.printStackTrace()
   }
}

fun seedDirectory(): Path = seedDirectory

internal val seedDirectory: Path by lazy {
   val baseDir = System.getenv("XDG_CACHE_HOME")?.ifBlank { null }
      ?: System.getProperty("user.home")

   val kotestConfigDir = Path(baseDir).resolve(".kotest")

   kotestConfigDir.resolve("seeds").apply {
      createDirectories()
   }
}

private fun DescriptorPath.seedPath(): Path =
   seedDirectory.resolve(seedFileName())

private fun DescriptorPath.seedFileName(): String =
   value.replace(Regex("""[/\\<>:()]"""), "_")
