package io.kotest.engine.spec

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.listeners.IgnoredSpecListener
import io.kotest.core.listeners.InstantiationErrorListener
import io.kotest.core.listeners.InstantiationListener
import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.functionOverrideCallbacks
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.mapError
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * Used to invoke extension points / listeners / callbacks on specs.
 */
internal class SpecExtensions(private val registry: ExtensionRegistry) {

   private val logger = Logger(SpecExtensions::class)

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
      logger.log { Pair(spec::class.bestName(), "beforeSpec $spec") }

      val errors = extensions(spec)
         .filterIsInstance<BeforeSpecListener>()
         .mapNotNull { ext ->
            runCatching { ext.beforeSpec(spec) }
               .mapError { ExtensionException.BeforeSpecException(it) }
               .exceptionOrNull()
         }

      return when {
         errors.isEmpty() -> Result.success(spec)
         errors.size == 1 -> Result.failure(errors.first())
         else -> Result.failure(MultipleExceptions(errors))
      }
   }

   /**
    * Runs all the after spec listeners for this [Spec]. All errors are caught and wrapped
    * in [AfterSpecListener] and if more than one error, all will be returned as a [MultipleExceptions].
    */
   suspend fun afterSpec(spec: Spec): Result<Spec> = runCatching {
      logger.log { Pair(spec::class.bestName(), "afterSpec $spec") }

      spec.registeredAutoCloseables().let { closeables ->
         logger.log { Pair(spec::class.bestName(), "Closing ${closeables.size} autocloseables [$closeables]") }
         closeables.forEach {
            if (it.isInitialized()) it.value.close() else Unit
         }
      }

      val errors = extensions(spec).filterIsInstance<AfterSpecListener>().mapNotNull { ext ->
         runCatching { ext.afterSpec(spec) }
            .mapError { ExtensionException.AfterSpecException(it) }.exceptionOrNull()
      }

      return when {
         errors.isEmpty() -> Result.success(spec)
         errors.size == 1 -> Result.failure(errors.first())
         else -> Result.failure(MultipleExceptions(errors))
      }
   }

   suspend fun specInstantiated(spec: Spec) = runCatching {
      logger.log { Pair(spec::class.bestName(), "specInstantiated $spec") }
      registry.all().filterIsInstance<InstantiationListener>().forEach { it.specInstantiated(spec) }
   }

   suspend fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) = runCatching {
      logger.log { Pair(kclass.bestName(), "specInstantiationError $t") }
      registry.all().filterIsInstance<InstantiationErrorListener>().forEach { it.instantiationError(kclass, t) }
   }

   suspend fun prepareSpec(kclass: KClass<out Spec>): Result<KClass<*>> {

      val exts = registry.all().filterIsInstance<PrepareSpecListener>()
      logger.log { Pair(kclass.bestName(), "prepareSpec (${exts.size})") }

      val errors = exts.mapNotNull {
         runCatching { it.prepareSpec(kclass) }
            .mapError { ExtensionException.PrepareSpecException(it) }.exceptionOrNull()
      }

      return when {
         errors.isEmpty() -> Result.success(kclass)
         errors.size == 1 -> Result.failure(errors.first())
         else -> Result.failure(MultipleExceptions(errors))
      }
   }

   suspend fun finalizeSpec(
      kclass: KClass<out Spec>,
      results: Map<TestCase, TestResult>,
      t: Throwable?
   ): Result<KClass<out Spec>> {

      val exts = registry.all().filterIsInstance<FinalizeSpecListener>()
      logger.log { Pair(kclass.bestName(), "finishSpec (${exts.size}) results:$results") }

      val errors = exts.mapNotNull {
         runCatching { it.finalizeSpec(kclass, results) }
            .mapError { ExtensionException.FinalizeSpecException(it) }.exceptionOrNull()
      }

      return when {
         errors.isEmpty() -> Result.success(kclass)
         errors.size == 1 -> Result.failure(errors.first())
         else -> Result.failure(MultipleExceptions(errors))
      }
   }

   suspend fun <T> intercept(spec: Spec, f: suspend () -> T): T? {

      val exts = extensions(spec).filterIsInstance<SpecExtension>()
      logger.log { Pair(spec::class.bestName(), "Intercepting spec with ${exts.size} spec extensions") }

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
      return result
   }

   /**
    * Notify all [IgnoredSpecListener]s that the given [kclass] has been ignored.
    */
   suspend fun ignored(kclass: KClass<out Spec>, reason: String?): Result<KClass<out Spec>> {

      val exts = registry.all().filterIsInstance<IgnoredSpecListener>()
      logger.log { Pair(kclass.bestName(), "ignored ${exts.size} extensions on $kclass") }

      val errors = exts.mapNotNull {
         runCatching { it.ignoredSpec(kclass, reason) }
            .mapError { ExtensionException.IgnoredSpecException(it) }.exceptionOrNull()
      }

      return when {
         errors.isEmpty() -> Result.success(kclass)
         errors.size == 1 -> Result.failure(errors.first())
         else -> Result.failure(MultipleExceptions(errors))
      }
   }
}
