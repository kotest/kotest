package io.kotest.property.seed

import io.kotest.assertions.print.print
import io.kotest.common.TestPath
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.notExists

actual fun readSeed(path: TestPath): Long? {
   val p = seedPath(path)
   if (p.notExists()) return null
   return try {
      Files.readAllLines(p).firstOrNull()?.trim()?.toLongOrNull()
   } catch (e: Exception) {
      println("Error reading seed")
      e.print()
      null
   }
}

fun xdgConfigHomeOrNull(): String? = System.getenv("XDG_CONFIG_HOME")?.takeIf { it.isNotBlank() }
fun userHome(): String = System.getProperty("user.home")
fun configDirectory(): String = xdgConfigHomeOrNull() ?: userHome()
fun seedDirectory(): Path = Paths.get(configDirectory()).resolve(".kotest").resolve("seeds")

fun seedPath(path: TestPath): Path {
   return seedDirectory().resolve(path.value)
}

actual fun writeSeed(path: TestPath, seed: Long) {
   val f = seedPath(path)
   f.parent.toFile().mkdirs()
   try {
      Files.write(f, seed.toString().encodeToByteArray())
   } catch (e: Exception) {
      println("Error writing seed")
      e.print()
   }
}

actual fun clearSeed(path: TestPath) {
   val f = seedPath(path)
   try {
      f.toFile().deleteRecursively()
   } catch (e: Exception) {
      println("Error clearing seed")
      e.print()
   }
}
