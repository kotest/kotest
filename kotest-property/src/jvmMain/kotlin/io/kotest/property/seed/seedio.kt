package io.kotest.property.seed

import io.kotest.framework.shared.test.TestPath
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.notExists

actual fun readSeed(path: TestPath): Long? {
   val p = seedPath(path)
   if (p.notExists()) return null
   return Files.readAllLines(p).firstOrNull()?.trim()?.toLongOrNull()
}

fun seedDirectory(): Path = Paths.get(System.getProperty("user.home")).resolve(".kotest").resolve("seeds")

fun seedPath(path: TestPath): Path {
   return seedDirectory().resolve(path.value)
}

actual fun writeSeed(path: TestPath, seed: Long) {
   val f = seedPath(path)
   f.parent.toFile().mkdirs()
   Files.write(f, seed.toString().encodeToByteArray())
}
