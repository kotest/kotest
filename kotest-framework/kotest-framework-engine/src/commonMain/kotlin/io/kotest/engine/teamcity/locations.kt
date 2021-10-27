package io.kotest.engine.teamcity

import io.kotest.core.SourceRef
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

object Locations {

   fun locationHint(kclass: KClass<*>): String =
      "kotest:class/" + kclass.bestName() + ":1"

   fun locationHint(sourceRef: SourceRef): String? = when (sourceRef) {
      is SourceRef.FileSource -> "kotest:file/${sourceRef.fileName}:1"
      is SourceRef.FileLineSource -> "kotest:file/${sourceRef.fileName}:${sourceRef.lineNumber}"
      is SourceRef.ClassLineSource -> "kotest:class/${sourceRef.fqn}:${sourceRef.lineNumber}"
      is SourceRef.ClassSource -> "kotest:class/${sourceRef.fqn}:1"
      SourceRef.None -> null
   }
}
