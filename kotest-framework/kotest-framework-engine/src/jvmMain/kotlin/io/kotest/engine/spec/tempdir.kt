package io.kotest.engine.spec

import io.kotest.core.TestConfiguration
import java.io.File

fun TestConfiguration.tempdir(prefix: String? = null, suffix: String? = null): File {
   val dir = kotlin.io.path.createTempDirectory((prefix ?: javaClass.name) + (suffix ?: "")).toFile()
   afterSpec {
      if (!dir.deleteRecursively())
         throw TempDirDeletionException(dir)
   }
   return dir
}

class TempDirDeletionException(val file: File) : Exception("Temp dir '$file' could not be deleted")
