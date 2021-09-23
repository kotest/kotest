package io.kotest.engine.spec

import io.kotest.core.config.Configuration
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.SpecIgnoredExtension
import io.kotest.core.extensions.SpecInactiveExtension
import io.kotest.core.extensions.SpecInitializeExtension
import io.kotest.core.extensions.SpecInstantiationExtension
import io.kotest.core.extensions.SpecInterceptExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.listeners.SpecInstantiationListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.functionOverrideCallbacks
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.mpp.log
import kotlin.reflect.KClass

/**
 * Used to invoke extension points / listeners / callbacks on specs.
 */
internal class SpecExtensions(private val configuration: Configuration) {

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

   suspend fun specInitialize(spec: Spec): Result<Unit> = runCatching {
      extensions(spec).filterIsInstance<SpecInitializeExtension>().forEach { it.initialize(spec) }
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

   suspend fun specInstantiated(spec: Spec) = runCatching {
      log { "SpecExtensions: specInstantiated spec:$spec" }
      configuration.extensions().filterIsInstance<SpecInstantiationListener>().forEach { it.specInstantiated(spec) }
      configuration.extensions().filterIsInstance<SpecInstantiationExtension>().forEach { it.onSpecInstantiation(spec) }
   }

   suspend fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) = kotlin.runCatching {
      log { "SpecExtensions: specInstantiationError $kclass errror:$t" }
      configuration.extensions().filterIsInstance<SpecInstantiationListener>().forEach { it.specInstantiationError(kclass, t) }
      configuration.extensions().filterIsInstance<SpecInstantiationExtension>().forEach { it.onSpecInstantiationError(kclass, t) }
   }

   suspend fun inactiveSpec(spec: Spec, results: Map<TestCase, TestResult>) {
      configuration.extensions().filterIsInstance<SpecInactiveExtension>().forEach { it.inactive(spec, results) }
   }

   suspend fun finishSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      val exts = configuration.extensions().filterIsInstance<FinalizeSpecListener>()
      log { "SpecExtensions: finishSpec(${exts.size}) $kclass results:$results" }
      exts.forEach { it.finalizeSpec(kclass, results) }
   }

   suspend fun intercept(spec: Spec, f: suspend (Spec) -> Unit) {
      val exts = configuration.extensions().filterIsInstance<SpecInterceptExtension>()
      val initial: suspend (Spec) -> Unit = { f(it) }
      val chain = exts.foldRight(initial) { op, acc -> { s -> op.interceptSpec(s) { acc(it) } } }
      chain.invoke(spec)
   }

   suspend fun ignored(kclass: KClass<out Spec>) {
      val exts = configuration.extensions().filterIsInstance<SpecIgnoredExtension>()
      log { "SpecExtensions: ignored(${exts.size}) $kclass" }
      exts.forEach { it.ignored(kclass, null) }
   }
}

class BeforeSpecListenerException(throwable: Throwable) : RuntimeException(throwable)
class AfterSpecListenerException(throwable: Throwable) : RuntimeException(throwable)
