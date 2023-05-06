package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.common.Platform
import io.kotest.common.flatMap
import io.kotest.common.platform
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.toProjectContext
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.interceptor.ref.ApplyExtensionsInterceptor
import io.kotest.engine.spec.interceptor.ConfigurationInContextSpecInterceptor
import io.kotest.engine.spec.interceptor.ref.EnabledIfInterceptor
import io.kotest.engine.spec.interceptor.ref.FinalizeSpecInterceptor
import io.kotest.engine.spec.interceptor.IgnoreNestedSpecStylesInterceptor
import io.kotest.engine.spec.interceptor.ref.IgnoredSpecInterceptor
import io.kotest.engine.spec.interceptor.InlineTagSpecInterceptor
import io.kotest.engine.spec.interceptor.ref.PrepareSpecInterceptor
import io.kotest.engine.spec.interceptor.ProjectContextInterceptor
import io.kotest.engine.spec.interceptor.ref.RequiresTagInterceptor
import io.kotest.engine.spec.interceptor.SpecExtensionInterceptor
import io.kotest.engine.spec.interceptor.ref.SpecFilterInterceptor
import io.kotest.engine.spec.interceptor.ref.SpecFinishedInterceptor
import io.kotest.engine.spec.interceptor.ref.SpecRefExtensionInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.engine.spec.interceptor.ref.RequiresPlatformInterceptor
import io.kotest.engine.spec.interceptor.ref.SpecStartedInterceptor
import io.kotest.engine.spec.interceptor.ref.SystemPropertySpecFilterInterceptor
import io.kotest.engine.spec.interceptor.ref.TagsInterceptor
import io.kotest.engine.specInterceptorsForPlatform
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * Executes a single [SpecRef].
 *
 * Uses a [TestEngineListener] to notify of events in the spec lifecycle.
 *
 * The spec executor has two levels of interceptors:
 * [io.kotest.engine.spec.interceptor.SpecRefInterceptor] are executed before the spec is created.
 * [io.kotest.engine.spec.interceptor.SpecInterceptor] are executed after the spec is created.
 *
 */
@ExperimentalKotest
class SpecExecutor(
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val context: EngineContext,
) {

   private val logger = Logger(SpecExecutorDelegate::class)
   private val extensions = SpecExtensions(context.configuration.registry)
   private val listener = context.listener

   suspend fun execute(ref: SpecRef) {
      logger.log { Pair(ref.kclass.bestName(), "Received $ref") }
      referenceInterceptors(ref)
   }

   suspend fun execute(kclass: KClass<out Spec>) {
      execute(SpecRef.Reference(kclass))
   }

   private suspend fun referenceInterceptors(ref: SpecRef) {

      val interceptors = listOfNotNull(
         RequiresPlatformInterceptor(listener, context, context.configuration.registry),
         if (platform == Platform.JVM) EnabledIfInterceptor(listener, context.configuration.registry) else null,
         IgnoredSpecInterceptor(listener, context.configuration.registry),
         SpecFilterInterceptor(listener, context.configuration.registry),
         SystemPropertySpecFilterInterceptor(listener, context.configuration.registry),
         TagsInterceptor(listener, context.configuration),
         if (platform == Platform.JVM) RequiresTagInterceptor(listener, context.configuration, context.configuration.registry) else null,
         SpecRefExtensionInterceptor(context.configuration.registry),
         SpecStartedInterceptor(listener),
         SpecFinishedInterceptor(listener),
         if (platform == Platform.JVM) ApplyExtensionsInterceptor(context.configuration.registry) else null,
         PrepareSpecInterceptor(context.configuration.registry),
         FinalizeSpecInterceptor(context.configuration.registry),
      )

      val innerExecute: suspend (SpecRef) -> Result<Map<TestCase, TestResult>> = {
         createInstance(ref).flatMap { specInterceptors(it) }
      }

      logger.log { Pair(ref.kclass.bestName(), "Executing ${interceptors.size} reference interceptors") }
      interceptors.foldRight(innerExecute) { ext: SpecRefInterceptor, fn: suspend (SpecRef) -> Result<Map<TestCase, TestResult>> ->
         { ref -> ext.intercept(ref, fn) }
      }.invoke(ref)
   }

   private suspend fun specInterceptors(spec: Spec): Result<Map<TestCase, TestResult>> {

      val interceptors = listOfNotNull(
         if (platform == Platform.JS) IgnoreNestedSpecStylesInterceptor(listener, context.configuration.registry) else null,
         ProjectContextInterceptor(context.toProjectContext()),
         SpecExtensionInterceptor(context.configuration.registry),
         ConfigurationInContextSpecInterceptor(context.configuration),
         InlineTagSpecInterceptor(listener, context.configuration),
      ) + specInterceptorsForPlatform()

      val initial: suspend (Spec) -> Result<Map<TestCase, TestResult>> = {
         try {
            val delegate = createSpecExecutorDelegate(listener, defaultCoroutineDispatcherFactory, context.configuration)
            logger.log { Pair(spec::class.bestName(), "delegate=$delegate") }
            Result.success(delegate.execute(spec))
         } catch (t: Throwable) {
            logger.log { Pair(spec::class.bestName(), "Error executing spec $t") }
            Result.failure(t)
         }
      }

      logger.log { Pair(spec::class.bestName(), "Executing ${interceptors.size} spec interceptors") }
      return interceptors.foldRight(initial) { ext, fn ->
         { spec -> ext.intercept(spec, fn) }
      }.invoke(spec)
   }

   /**
    * Creates an instance of the given [SpecRef], notifies users of the instantiation event
    * or instantiation failure, and returns a Result with the error or spec.
    *
    * After this method is called the spec is sealed.
    */
   private suspend fun createInstance(ref: SpecRef): Result<Spec> =
      ref.instance(context.configuration.registry)
         .onFailure { extensions.specInstantiationError(ref.kclass, it) }
         .flatMap { spec -> extensions.specInstantiated(spec).map { spec } }
         .onSuccess { if (it is DslDrivenSpec) it.seal() }
}

interface SpecExecutorDelegate {
   suspend fun execute(spec: Spec): Map<TestCase, TestResult>
}

@ExperimentalKotest
internal expect fun createSpecExecutorDelegate(
   listener: TestEngineListener,
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   configuration: ProjectConfiguration,
): SpecExecutorDelegate

