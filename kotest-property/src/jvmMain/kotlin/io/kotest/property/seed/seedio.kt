package io.kotest.property.seed

import io.kotest.assertions.print.print
import io.kotest.common.TestPath
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.readLines
import kotlin.io.path.writeText


internal actual fun readSeed(path: TestPath): Long? {
   return try {
      return path.seedPath()
         .takeIf { it !in seedsMarkedForDeletion && it.exists() }
         ?.readLines()
         ?.firstOrNull()
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
      f.writeText(seed.toString())
      seedsMarkedForDeletion.remove(f)
   } catch (e: Exception) {
      println("Error writing seed $seed for $path")
      e.printStackTrace()
   }
}

internal actual fun clearSeed(path: TestPath) {
   try {
      val f = path.seedPath()
      seedsMarkedForDeletion.add(f)
   } catch (e: Exception) {
      println("Error clearing seed for $path")
      e.printStackTrace()
   }
}

private val seedsMarkedForDeletion = mutableSetOf<Path>()

internal actual suspend fun cleanUpSeedFiles(): Unit = coroutineScope {
   seedsMarkedForDeletion
      .map { path ->
         async {
            try {
               if (
                  path.isRegularFile()
                  && path != seedDirectory
                  && path.startsWith(seedDirectory)
               ) {
                  path.deleteIfExists()
               }
            } catch (e: Exception) {
               println("Error deleting seed $path")
               e.printStackTrace()
            }
         }
      }
      .awaitAll()
}


internal fun seedDirectory(): Path = seedDirectory

private val kotestConfigDir by lazy {
   Path(
      System.getenv("XDG_CACHE_HOME")
         ?.ifBlank { null }
         ?: System.getProperty("user.home")
   ).apply {
      createDirectories()
   }
}

private val seedDirectory: Path by lazy {
   kotestConfigDir.resolve(".kotest/seeds/").apply {
      createDirectories()
   }
}

private fun TestPath.seedPath(): Path {
   return testPathsValueCache.getOrPut(this) {
      seedDirectory.resolve(escapeValue())
   }
}

private val testPathsValueCache = WeakHashMap<TestPath, Path>()

private fun TestPath.escapeValue(): String =
   value.replace(Regex("""[/\\<>:()]"""), "_")
