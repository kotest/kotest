package io.kotest.property.seed

import io.kotest.assertions.print.print
import io.kotest.common.TestPath
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText
import kotlin.io.path.writeText


internal actual fun readSeed(path: TestPath): Long? {
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

internal actual fun writeSeed(path: TestPath, seed: Long) {
   try {
      val f = path.seedPath()
      f.createParentDirectories()
      f.writeText(seed.toString())
   } catch (e: Exception) {
      println("Error writing seed $seed for $path")
      e.printStackTrace()
   }
}

internal actual fun clearSeed(path: TestPath) {
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

internal fun seedDirectory(): Path = seedDirectory

private val kotestConfigDir by lazy {
   val xdgCacheHome = System.getenv("XDG_CACHE_HOME")?.ifBlank { null }

   Path(
      xdgCacheHome ?: System.getProperty("user.home")
   ).apply {
      createDirectories()
   }
}

private val seedDirectory: Path by lazy {
   kotestConfigDir.resolve(".kotest/seeds/").apply {
      createDirectories()
   }
}

private fun TestPath.seedPath(): Path =
   seedDirectory.resolve(seedFileName())

private fun TestPath.seedFileName(): String =
   value.replace(Regex("""[/\\<>:()]"""), "_")
