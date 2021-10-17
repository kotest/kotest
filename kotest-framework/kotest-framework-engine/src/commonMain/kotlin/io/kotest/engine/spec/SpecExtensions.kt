package io.kotest.engine.spec

import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.listeners.InactiveSpecListener
import io.kotest.core.listeners.InstantiationErrorListener
import io.kotest.core.listeners.InstantiationListener
import io.kotest.core.listeners.IgnoredSpecListener
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
internal class SpecExtensions(private val extensions: List<Extension>) {

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
         extensions // globals
   }

   suspend fun beforeSpec(spec: Spec): Result<Spec> {
      log { "SpecExtensions: beforeSpec $spec" }
      return runCatching {
         extensions(spec).filterIsInstance<BeforeSpecListener>().forEach { it.beforeSpec(spec) }
         spec
      }.fold({ Result.success(it) }, { Result.failure(BeforeSpecListenerException(it)) })
   }

   suspend fun afterSpec(spec: Spec): Result<Spec> = runCatching {
      log { "SpecExtensions: afterSpec $spec" }

      spec.registeredAutoCloseables().let { closeables ->
         log { "Closing ${closeables.size} autocloseables [$closeables]" }
         closeables.forEach { it.value.close() }
      }

      return runCatching {
         extensions(spec).filterIsInstance<AfterSpecListener>().forEach { it.afterSpec(spec) }
         spec
      }.fold({ Result.success(it) }, { Result.failure(AfterSpecListenerException(it)) })
   }

   suspend fun specInstantiated(spec: Spec) = runCatching {
      log { "SpecExtensions: specInstantiated spec:$spec" }
      extensions.filterIsInstance<SpecInstantiationListener>().forEach { it.specInstantiated(spec) }
      extensions.filterIsInstance<InstantiationListener>().forEach { it.specInstantiated(spec) }
   }

   suspend fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) = runCatching {
      log { "SpecExtensions: specInstantiationError $kclass errror:$t" }
      extensions.filterIsInstance<SpecInstantiationListener>().forEach { it.specInstantiationError(kclass, t) }
      extensions.filterIsInstance<InstantiationErrorListener>().forEach { it.instantiationError(kclass, t) }
   }

   suspend fun inactiveSpec(spec: Spec, results: Map<TestCase, TestResult>) {
      extensions.filterIsInstance<InactiveSpecListener>().forEach { it.inactive(spec, results) }
   }

   suspend fun finishSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      val exts = extensions.filterIsInstance<FinalizeSpecListener>()
      log { "SpecExtensions: finishSpec ${exts.size} extensions on $kclass results:$results" }
      exts.forEach { it.finalizeSpec(kclass, results) }
   }

   suspend fun intercept(spec: Spec, f: suspend () -> Unit) {
      val exts = extensions(spec).filterIsInstance<SpecExtension>()
      val initial: suspend () -> Unit = { f() }
      val chain = exts.foldRight(initial) { op, acc ->
         {
            op.intercept(spec::class) {
               op.intercept(spec) {
                  acc()
               }
            }
         }
      }
      chain.invoke()
   }

   /**
    * Notify all [IgnoredSpecListener]s that the given [kclass] has been ignored.
    */
   suspend fun ignored(kclass: KClass<out Spec>) {
      val exts = extensions.filterIsInstance<IgnoredSpecListener>()
      log { "SpecExtensions: ignored ${exts.size} extensions on $kclass" }
      exts.forEach { it.ignoredSpec(kclass, null) }
   }
}

class BeforeSpecListenerException(throwable: Throwable) : RuntimeException(throwable)
class AfterSpecListenerException(throwable: Throwable) : RuntimeException(throwable)
