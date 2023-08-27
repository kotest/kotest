package io.kotest.engine.spec

import io.kotest.core.TestConfiguration
import java.io.File

fun TestConfiguration.tempfile(prefix: String? = null, suffix: String? = null): File {
  
   val file = kotlin.io.path.createTempFile(prefix ?: javaClass.name, suffix).toFile()
   afterSpec {
      if (!file.delete())
         throw TempFileDeletionException(file)
   }
   return file
}

class TempFileDeletionException(val file: File) : Exception("Temp file '$file' could not be deleted")
