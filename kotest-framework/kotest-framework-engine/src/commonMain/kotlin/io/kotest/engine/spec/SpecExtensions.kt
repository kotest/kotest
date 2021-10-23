package io.kotest.engine.spec

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.listeners.IgnoredSpecListener
import io.kotest.core.listeners.InactiveSpecListener
import io.kotest.core.listeners.InstantiationErrorListener
import io.kotest.core.listeners.InstantiationListener
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
internal class SpecExtensions(private val registry: ExtensionRegistry) {

   /**
    * Returns all [Extension]s applicable to a [Spec]. This includes extensions via
    * function overrides, those registered explicitly in the spec as part of the DSL,
    * and project wide extensions from configuration.
    */
   fun extensions(spec: Spec): List<Extension> {
      return spec.extensions() + // overriding the extensions function in the spec
         spec.listeners() + // overriding the listeners function in the spec
         spec.functionOverrideCallbacks() + // dsl
         spec.registeredExtensions() + // added to the spec via register
         registry.all() // globals
   }

   suspend fun beforeSpec(spec: Spec): Result<Spec> {
      log { "SpecExtensions: beforeSpec $spec" }
      return runCatching {
         extensions(spec).filterIsInstance<BeforeSpecListener>().forEach { it.beforeSpec(spec) }
         spec
      }.fold({ Result.success(it) }, { Result.failure(BeforeSpecException(it)) })
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
      }.fold({ Result.success(it) }, { Result.failure(AfterSpecException(it)) })
   }

   suspend fun specInstantiated(spec: Spec) = runCatching {
      log { "SpecExtensions: specInstantiated spec:$spec" }
      registry.all().filterIsInstance<SpecInstantiationListener>().forEach { it.specInstantiated(spec) }
      registry.all().filterIsInstance<InstantiationListener>().forEach { it.specInstantiated(spec) }
   }

   suspend fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) = runCatching {
      log { "SpecExtensions: specInstantiationError $kclass errror:$t" }
      registry.all().filterIsInstance<SpecInstantiationListener>().forEach { it.specInstantiationError(kclass, t) }
      registry.all().filterIsInstance<InstantiationErrorListener>().forEach { it.instantiationError(kclass, t) }
   }

   suspend fun inactiveSpec(spec: Spec, results: Map<TestCase, TestResult>) {
      log { "SpecExtensions: inactiveSpec $spec" }
      registry.all().filterIsInstance<InactiveSpecListener>().forEach { it.inactive(spec, results) }
   }

   suspend fun finishSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      val exts = registry.all().filterIsInstance<FinalizeSpecListener>()
      log { "SpecExtensions: finishSpec ${exts.size} extensions on $kclass results:$results" }
      exts.forEach { it.finalizeSpec(kclass, results) }
   }

   suspend fun <T> intercept(spec: Spec, f: suspend () -> T): T {
      val exts = extensions(spec).filterIsInstance<SpecExtension>()
      var result: T? = null
      val initial: suspend () -> Unit = {
         result = f()
      }
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
      return result!!
   }

   /**
    * Notify all [IgnoredSpecListener]s that the given [kclass] has been ignored.
    */
   suspend fun ignored(kclass: KClass<out Spec>) {
      val exts = registry.all().filterIsInstance<IgnoredSpecListener>()
      log { "SpecExtensions: ignored ${exts.size} extensions on $kclass" }
      exts.forEach { it.ignoredSpec(kclass, null) }
   }
}

class BeforeSpecException(throwable: Throwable) : RuntimeException(throwable)
class AfterSpecException(throwable: Throwable) : RuntimeException(throwable)
