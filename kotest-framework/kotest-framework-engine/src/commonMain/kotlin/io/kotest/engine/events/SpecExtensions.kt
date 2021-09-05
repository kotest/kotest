package io.kotest.engine.events

import io.kotest.core.config.Configuration
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.SpecFinalizeExtension
import io.kotest.core.extensions.SpecInitializeExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.SpecIgnoredListener
import io.kotest.core.listeners.SpecInstantiationListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.fp.Try
import io.kotest.mpp.log

/**
 * Used to send invoke extension points on specs.
 */
class SpecExtensions(private val configuration: Configuration) {

   /**
    * Returns all [Extension]s applicable to the [Spec]. This includes extensions via the
    * function override, those registered explicitly in the spec, and project wide extensions
    * from configuration.
    */
   fun resolvedExtensions(spec: Spec): List<Extension> {
      return spec.extensions() + // overriding in the spec
         spec.registeredExtensions() + // registered on the spec
         configuration.extensions() // globals
   }

   fun specInitialize(spec: Spec): Result<Unit> = kotlin.runCatching {
      resolvedExtensions(spec).filterIsInstance<SpecInitializeExtension>().forEach { it.initialize(spec) }
   }

   fun specFinalize(spec: Spec): Try<Unit> = Try {
      resolvedExtensions(spec).filterIsInstance<SpecFinalizeExtension>().forEach { it.finalize(spec) }
   }

   suspend fun beforeSpec(spec: Spec): Try<Unit> = Try {
      resolvedExtensions(spec).filterIsInstance<BeforeSpecListener>().forEach { it.beforeSpec(spec) }
   }

   suspend fun afterSpec(spec: Spec): Try<Unit> = Try {
      resolvedExtensions(spec).filterIsInstance<AfterSpecListener>().forEach { it.afterSpec(spec) }
   }

   fun specInstantiated(spec: Spec) = kotlin.runCatching {
      log { "SpecExtensions: specInstantiated spec:$spec" }
      val listeners = configuration.extensions().filterIsInstance<SpecInstantiationListener>()
      listeners.forEach { it.specInstantiated(spec) }
   }

   suspend fun specSkipped(spec: Spec, results: Map<TestCase, TestResult>) {
      configuration.extensions().filterIsInstance<SpecIgnoredListener>().forEach {
         it.specIgnored(spec, results)
      }
   }
}
