package io.kotest.engine.spec

import io.kotest.core.TestConfiguration
import java.io.File

fun TestConfiguration.tempdir(prefix: String? = javaClass.name, suffix: String? = null): File {
   val dir = kotlin.io.path.createTempDirectory(prefix ?: "tmp")
   afterSpec {
      dir.toFile().delete()
   }
   return dir.toFile()
}
