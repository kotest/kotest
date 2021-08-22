package io.kotest.engine.events

import io.kotest.core.config.Configuration
import io.kotest.core.extensions.AfterSpecExtension
import io.kotest.core.extensions.BeforeSpecExtension
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.SpecFinalizeExtension
import io.kotest.core.extensions.SpecInitializeExtension
import io.kotest.core.spec.Spec
import io.kotest.fp.Try

/**
 * Used to send invoke extension points on specs.
 */
class Extensions(private val configuration: Configuration) {

   /**
    * Returns all [Extension]s applicable to the [Spec]. This includes extensions via the
    * function override, those registered explicitly in the spec, and project wide extensions
    * from configuration.
    */
   private fun resolvedExtensions(spec: Spec): List<Extension> {
      return spec.extensions() + // overriding in the spec
         spec.registeredExtensions() + // registered on the spec
         configuration.extensions() // globals
   }

   fun specInitialize(spec: Spec): Try<Unit> = Try {
      resolvedExtensions(spec).filterIsInstance<SpecInitializeExtension>().forEach { it.initialize(spec) }
   }

   fun specFinalize(spec: Spec): Try<Unit> = Try {
      resolvedExtensions(spec).filterIsInstance<SpecFinalizeExtension>().forEach { it.finalize(spec) }
   }

   fun beforeSpec(spec: Spec): Try<Unit> = Try {
      resolvedExtensions(spec).filterIsInstance<BeforeSpecExtension>().forEach { it.beforeSpec(spec) }
   }

   fun afterSpec(spec: Spec): Try<Unit> = Try {
      resolvedExtensions(spec).filterIsInstance<AfterSpecExtension>().forEach { it.afterSpec(spec) }
   }
}
