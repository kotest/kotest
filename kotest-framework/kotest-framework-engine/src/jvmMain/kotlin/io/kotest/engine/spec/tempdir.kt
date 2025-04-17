package io.kotest.engine.spec

import io.kotest.core.TestConfiguration
import java.io.File
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively

@OptIn(ExperimentalPathApi::class)
fun TestConfiguration.tempdir(prefix: String? = null, suffix: String? = null, keepOnFailure: Boolean = false): File {
   val dir = kotlin.io.path.createTempDirectory((prefix ?: javaClass.name) + (suffix ?: "")).toFile()
   var hasErrors = false
   afterAny { (_, result) -> if (result.isErrorOrFailure) hasErrors = true }
   afterSpec {
      runCatching {
         if (keepOnFailure && hasErrors) Unit else dir.toPath().deleteRecursively()
      }.onFailure {
         throw TempDirDeletionException(dir)
      }
   }
   return dir
}

class TempDirDeletionException(val file: File) : Exception("Temp dir '$file' could not be deleted")
