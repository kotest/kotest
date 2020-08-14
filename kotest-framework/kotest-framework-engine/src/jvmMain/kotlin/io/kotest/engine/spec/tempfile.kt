package io.kotest.engine.spec

import java.io.File
import java.nio.file.Files

fun TestSuite.tempfile(prefix: String? = null, suffix: String? = ".tmp"): File {
   val file = Files.createTempFile(prefix, suffix).toFile()
   afterSpec {
      file.delete()
   }
   return file
}
