package io.kotest.engine.spec

import io.kotest.core.TestConfiguration
import java.io.File

fun TestConfiguration.tempdir(prefix: String? = null, suffix: String? = null): File {
   val dir = kotlin.io.path.createTempDirectory(prefix ?: javaClass.name).toFile()
   afterSpec {
      dir.deleteRecursively()
   }
   return dir
}
