package io.kotest.engine.teamcity

import io.kotest.core.SourceRef
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

object Locations {

   fun locationHint(kclass: KClass<*>): String =
      "kotest:class://" + kclass.bestName() + ":1"

   // note that everything before the :// is considered the "protocol" by the intellij plugin
   private fun fileHint(fileName: String, lineNumber: Int) = "kotest:file://${fileName}:${lineNumber}"
   private fun classHint(fqn: String, lineNumber: Int) = "kotest:class://${fqn}:${lineNumber}"

   fun locationHint(sourceRef: SourceRef): String? = when (sourceRef) {
      is SourceRef.FileLineSource -> fileHint(sourceRef.fileName, sourceRef.lineNumber)
      is SourceRef.FileSource -> fileHint(sourceRef.fileName, 1)
      is SourceRef.ClassLineSource -> classHint(sourceRef.fqn, sourceRef.lineNumber)
      is SourceRef.ClassSource -> classHint(sourceRef.fqn, 1)
      SourceRef.None -> null
   }
}
