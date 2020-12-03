package io.kotest.engine.spec

import io.kotest.core.TestConfiguration
import java.io.File

fun TestConfiguration.tempdir(prefix: String? = null, suffix: String? = null): File {
   val dir = createTempDir(prefix ?: "tmp", suffix)
   afterSpec {
      dir.delete()
   }
   return dir
}
