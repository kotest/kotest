package io.kotest.property.seed

import io.kotest.assertions.print.print
import io.kotest.common.TestPath
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

actual fun readSeed(path: TestPath): Long? {
   return try {
      val p = seedPath(path)
      if (p.exists())
         Files.readAllLines(p).firstOrNull()?.trim()?.toLongOrNull()
      else null
   } catch (e: Exception) {
      println("Error reading seed")
      e.print()
      null
   }
}

internal fun xdgCacheHomeOrNull(): String? = System.getenv("XDG_CACHE_HOME")?.takeIf { it.isNotBlank() }
internal fun userHome(): String = System.getProperty("user.home")
internal fun configDirectory(): String = xdgCacheHomeOrNull() ?: userHome()
internal fun seedDirectory(): Path = Paths.get(configDirectory()).resolve(".kotest").resolve("seeds")

fun seedPath(path: TestPath): Path {
   return seedDirectory().resolve(escape(path.value))
}

private fun escape(path: String) = path
   .replace('/', '_')
   .replace('\\', '_')
   .replace('<', '_')
   .replace('>', '_')
   .replace(':', '_')
   .replace('(', '_')
   .replace(')', '_')

actual fun writeSeed(path: TestPath, seed: Long) {
   try {
      val f = seedPath(path)
      f.parent.toFile().mkdirs()
      Files.write(f, seed.toString().encodeToByteArray())
   } catch (e: Exception) {
      println("Error writing seed")
      e.printStackTrace()
   }
}

actual fun clearSeed(path: TestPath) {
   try {
      val f = seedPath(path)
      f.toFile().deleteRecursively()
   } catch (e: Exception) {
      println("Error clearing seed")
      e.printStackTrace()
   }
}
