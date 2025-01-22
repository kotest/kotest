package io.kotest.engine.teamcity

import io.kotest.core.source.SourceRef
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

object Locations {

   fun location(kclass: KClass<*>): String = classHint(kclass.bestName() ?: "", 1)

   fun location(sourceRef: SourceRef): String? = when (sourceRef) {
      is SourceRef.ClassSource -> classHint(sourceRef.fqn, sourceRef.lineNumber ?: 1)
      SourceRef.None -> null
   }

   // note that everything before the :// is considered the "protocol" by the intellij plugin
   private fun classHint(fqn: String, lineNumber: Int) = "kotest://${fqn}:${lineNumber}"
}
