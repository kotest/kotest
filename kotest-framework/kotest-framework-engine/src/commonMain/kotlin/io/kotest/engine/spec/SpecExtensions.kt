package io.kotest.engine.spec

import io.kotest.core.Logger
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
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.mapError
import io.kotest.common.reflection.bestName
import kotlin.reflect.KClass

/**
 * Used to invoke [Extension]s on specs.
 */
internal class SpecExtensions(
   private val specConfigResolver: SpecConfigResolver,
   private val projectConfigResolver: ProjectConfigResolver,
) {

   constructor() : this(SpecConfigResolver(), ProjectConfigResolver())

   private val logger = Logger(SpecExtensions::class)

   /**
    * Runs all the [BeforeSpecListener]s for this [Spec]. All errors are caught and wrapped
    * in [ExtensionException.BeforeSpecException] and if more than one error,
    * all will be wrapped in a [MultipleExceptions].
    */
   suspend fun beforeSpec(spec: Spec): Result<Spec> {
      logger.log { Pair(spec::class.bestName(), "beforeSpec $spec") }

      val errors = specConfigResolver.extensions(spec)
         .filterIsInstance<BeforeSpecListener>()
         .mapNotNull { listener ->
            runCatching { listener.beforeSpec(spec) }
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
    * Runs all the [AfterSpecListener]s for this [Spec]. All errors are caught and wrapped
    * in [ExtensionException.AfterSpecException] and if more than one error,
    * all will be wrapped in a [MultipleExceptions].
    */
   suspend fun afterSpec(spec: Spec): Result<Spec> = runCatching {
      logger.log { Pair(spec::class.bestName(), "afterSpec $spec") }

      spec.autoCloseables().let { closeables ->
         logger.log { Pair(spec::class.bestName(), "Closing ${closeables.size} autocloseables [$closeables]") }
         closeables.forEach {
            if (it.isInitialized()) it.value.close() else Unit
         }
      }

      val errors = specConfigResolver.extensions(spec)
         .filterIsInstance<AfterSpecListener>()
         .mapNotNull { listener ->
            runCatching { listener.afterSpec(spec) }
               .mapError { ExtensionException.AfterSpecException(it) }
               .exceptionOrNull()
         }

      return when {
         errors.isEmpty() -> Result.success(spec)
         errors.size == 1 -> Result.failure(errors.first())
         else -> Result.failure(MultipleExceptions(errors))
      }
   }

   suspend fun specInstantiated(spec: Spec) = runCatching {
      logger.log { Pair(spec::class.bestName(), "specInstantiated $spec") }
      specConfigResolver.extensions(spec)
         .filterIsInstance<InstantiationListener>()
         .forEach { it.specInstantiated(spec) }
   }

   suspend fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) = runCatching {
      logger.log { Pair(kclass.bestName(), "specInstantiationError $t") }
      projectConfigResolver.extensions()
         .filterIsInstance<InstantiationErrorListener>()
         .forEach { it.instantiationError(kclass, t) }
   }

   /**
    * Runs all the [PrepareSpecListener]s for this [Spec]. All errors are caught and wrapped
    * in [ExtensionException.PrepareSpecException] and if more than one error,
    * all will be wrapped in a [MultipleExceptions].
    */
   suspend fun prepareSpec(kclass: KClass<out Spec>): Result<KClass<*>> {

      val exts = projectConfigResolver.extensions().filterIsInstance<PrepareSpecListener>()
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

   /**
    * Runs all the [FinalizeSpecListener]s for this [Spec]. All errors are caught and wrapped
    * in [ExtensionException.FinalizeSpecException] and if more than one error,
    * all will be wrapped in a [MultipleExceptions].
    */
   suspend fun finalizeSpec(
      kclass: KClass<out Spec>,
      results: Map<TestCase, TestResult>,
      t: Throwable?
   ): Result<KClass<out Spec>> {

      val exts = projectConfigResolver.extensions().filterIsInstance<FinalizeSpecListener>()
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

      val exts = specConfigResolver.extensions(spec).filterIsInstance<SpecExtension>()
      logger.log { Pair(spec::class.bestName(), "Intercepting spec with ${exts.size} spec extensions") }

      var result: T? = null
      val initial: suspend () -> Unit = {
         result = f()
      }
      val chain = exts.foldRight(initial) { op, acc ->
         {
            op.intercept(spec) {
               acc()
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

      val exts = projectConfigResolver.extensions().filterIsInstance<IgnoredSpecListener>()
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
