package io.kotest.engine.spec

import io.kotest.core.config.Configuration
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.SpecFinalizeExtension
import io.kotest.core.extensions.SpecInitializeExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.InactiveSpecListener
import io.kotest.core.listeners.SpecInstantiationListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.functionOverrideCallbacks
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.fp.Try
import io.kotest.mpp.log
import kotlin.reflect.KClass

/**
 * Used to invoke extension points / listeners / callbacks on specs.
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

   suspend fun beforeSpec(spec: Spec): Result<Spec> {
      log { "SpecExtensions: beforeSpec $spec" }
      return kotlin.runCatching {
         extensions(spec).filterIsInstance<BeforeSpecListener>().forEach { it.beforeSpec(spec) }
         spec
      }.fold({ Result.success(it) }, { Result.failure(BeforeSpecListenerException(it)) })
   }

   suspend fun afterSpec(spec: Spec): Result<Spec> = kotlin.runCatching {
      log { "SpecExtensions: afterSpec $spec" }

      spec.registeredAutoCloseables().let { closeables ->
         log { "Closing ${closeables.size} autocloseables [$closeables]" }
         closeables.forEach { it.value.close() }
      }

      return kotlin.runCatching {
         extensions(spec).filterIsInstance<AfterSpecListener>().forEach { it.afterSpec(spec) }
         spec
      }.fold({ Result.success(it) }, { Result.failure(AfterSpecListenerException(it)) })
   }

   fun specInstantiated(spec: Spec) = kotlin.runCatching {
      log { "SpecExtensions: specInstantiated spec:$spec" }
      val listeners = configuration.extensions().filterIsInstance<SpecInstantiationListener>()
      listeners.forEach { it.specInstantiated(spec) }
   }

   fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) = kotlin.runCatching {
      log { "SpecExtensions: specInstantiationError kclass:${kclass} errror:$t" }
      val listeners = configuration.extensions().filterIsInstance<SpecInstantiationListener>()
      listeners.forEach { it.specInstantiationError(kclass, t) }
   }

   suspend fun inactiveSpec(spec: Spec, results: Map<TestCase, TestResult>) {
      configuration.extensions().filterIsInstance<InactiveSpecListener>().forEach { it.specInactive(spec, results) }
   }
}

class BeforeSpecListenerException(throwable: Throwable) : RuntimeException(throwable)
class AfterSpecListenerException(throwable: Throwable) : RuntimeException(throwable)
