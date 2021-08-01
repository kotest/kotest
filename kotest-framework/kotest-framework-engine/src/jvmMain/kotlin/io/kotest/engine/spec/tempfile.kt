package io.kotest.engine.spec

import io.kotest.core.TestConfiguration
import java.io.File
import java.nio.file.Files

fun TestConfiguration.tempfile(prefix: String? = javaClass.name, suffix: String? = ".tmp"): File {
   val file = Files.createTempFile(prefix, suffix).toFile()
   afterSpec {
      if (!file.delete())
         throw TempFileDeletionException(file)
   }
   return file
}

class TempFileDeletionException(val file: File) : Exception("Temp file '$file' could not be deleted")
