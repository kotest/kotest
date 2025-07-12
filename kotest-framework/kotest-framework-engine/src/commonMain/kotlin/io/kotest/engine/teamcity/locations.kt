package io.kotest.engine.teamcity

import io.kotest.core.source.SourceRef
import io.kotest.core.spec.SpecRef
import io.kotest.mpp.bestName

/**
 * Generates the location hint for a given class or source reference used by TeamCity service messages.
 */
object Locations {

   /**
    * Returns the location for a given [SourceRef] that is created when a test block is invoked in the DSL.
    */
   fun location(ref: SourceRef): String? = when (ref) {
      is SourceRef.ClassSource -> classHint(ref.fqn, 1)
      is SourceRef.ClassLineSource -> classHint(ref.fqn, ref.lineNumber ?: 1)
      SourceRef.None -> null
   }

   /**
    * Returns the location for a given [SpecRef] that contains a reference to a spec class.
    */
   fun location(ref: SpecRef): String? = when (ref) {
      is SpecRef.Function -> classHint(ref.fqn, 1)
      is SpecRef.Reference -> classHint(ref.kclass.bestName(), 1)
   }

   // note that everything before the :// is considered the "protocol" by the intellij plugin
   private fun classHint(fqn: String, lineNumber: Int) = "kotest://${fqn}:${lineNumber}"
}
