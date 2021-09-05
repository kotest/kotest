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
import io.kotest.core.spec.functionOverrideCallbacks
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.fp.Try
import io.kotest.mpp.log

/**
 * Used to invoke extension points on specs.
 */
class SpecExtensions(private val configuration: Configuration) {

   /**
    * Returns all [Extension]s applicable to the [Spec]. This includes extensions via
    * function overrides, those registered explicitly in the spec as part of the DSL,
    * and project wide extensions from configuration.
    */
   fun extensions(spec: Spec): List<Extension> {
      return spec.extensions() + // overriding in the spec
         spec.listeners() + // overriding in the spec
         spec.functionOverrideCallbacks() + // dsl
         spec.registeredExtensions() + // registered on the spec
         configuration.extensions() // globals
   }

   fun specInitialize(spec: Spec): Result<Unit> = kotlin.runCatching {
      extensions(spec).filterIsInstance<SpecInitializeExtension>().forEach { it.initialize(spec) }
   }

   fun specFinalize(spec: Spec): Try<Unit> = Try {
      extensions(spec).filterIsInstance<SpecFinalizeExtension>().forEach { it.finalize(spec) }
   }

   suspend fun beforeSpec(spec: Spec): Result<Spec> = kotlin.runCatching {
      log { "SpecExtensions: beforeSpec $spec" }
      extensions(spec).filterIsInstance<BeforeSpecListener>().forEach { it.beforeSpec(spec) }
      spec
   }

   suspend fun afterSpec(spec: Spec): Try<Unit> = Try {
      extensions(spec).filterIsInstance<AfterSpecListener>().forEach { it.afterSpec(spec) }
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
