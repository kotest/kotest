package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.common.Platform
import io.kotest.common.flatMap
import io.kotest.common.platform
import io.kotest.core.ProjectContext
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.Configuration
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.interceptor.ApplyExtensionsInterceptor
import io.kotest.engine.spec.interceptor.ConfigurationInContextInterceptor
import io.kotest.engine.spec.interceptor.EnabledIfSpecInterceptor
import io.kotest.engine.spec.interceptor.FinalizeSpecInterceptor
import io.kotest.engine.spec.interceptor.IgnoreNestedSpecStylesInterceptor
import io.kotest.engine.spec.interceptor.IgnoredSpecInterceptor
import io.kotest.engine.spec.interceptor.ProjectContextInterceptor
import io.kotest.engine.spec.interceptor.RequiresTagSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecExtensionInterceptor
import io.kotest.engine.spec.interceptor.SpecFilterInterceptor
import io.kotest.engine.spec.interceptor.SpecFinishedInterceptor
import io.kotest.engine.spec.interceptor.SpecRefExtensionInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecStartedInterceptor
import io.kotest.engine.spec.interceptor.SystemPropertySpecFilterInterceptor
import io.kotest.engine.spec.interceptor.TagsExcludedSpecInterceptor
import io.kotest.mpp.log
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
   private val listener: TestEngineListener,
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val conf: Configuration,
   private val projectContext: ProjectContext,
) {

   private val extensions = SpecExtensions(conf.registry())

   suspend fun execute(ref: SpecRef) {
      log { "SpecExecutor: Received $ref" }
      referenceInterceptors(ref)
   }

   suspend fun execute(kclass: KClass<out Spec>) {
      execute(ReflectiveSpecRef(kclass))
   }

   private suspend fun referenceInterceptors(ref: SpecRef) {

      val interceptors = listOf(
         EnabledIfSpecInterceptor(listener, conf.registry()),
         IgnoredSpecInterceptor(listener, conf.registry()),
         SpecFilterInterceptor(listener, conf.registry()),
         SystemPropertySpecFilterInterceptor(listener, conf.registry()),
         TagsExcludedSpecInterceptor(listener, conf),
         RequiresTagSpecInterceptor(listener, conf, conf.registry()),
         SpecRefExtensionInterceptor(conf.registry()),
         SpecStartedInterceptor(listener),
         SpecFinishedInterceptor(listener),
         ApplyExtensionsInterceptor(conf.registry()),
         PrepareSpecInterceptor(conf.registry()),
         FinalizeSpecInterceptor(conf.registry()),
      )

      val innerExecute: suspend (SpecRef) -> Result<Map<TestCase, TestResult>> = {
         createInstance(ref).flatMap { specInterceptors(it) }
      }

      log { "SpecExecutor: Executing ${interceptors.size} reference interceptors" }
      interceptors.foldRight(innerExecute) { ext: SpecRefInterceptor, fn: suspend (SpecRef) -> Result<Map<TestCase, TestResult>> ->
         { ref -> ext.intercept(ref, fn) }
      }.invoke(ref)
   }

   private suspend fun specInterceptors(spec: Spec): Result<Map<TestCase, TestResult>> {

      val interceptors = listOfNotNull(
         if (platform == Platform.JS) IgnoreNestedSpecStylesInterceptor(listener, conf.registry()) else null,
         ProjectContextInterceptor(projectContext),
         SpecExtensionInterceptor(conf.registry()),
         ConfigurationInContextInterceptor(conf),
      )

      val initial: suspend (Spec) -> Result<Map<TestCase, TestResult>> = {
         try {
            val delegate = createSpecExecutorDelegate(listener, defaultCoroutineDispatcherFactory, conf)
            log { "SpecExecutor: delegate=$delegate" }
            Result.success(delegate.execute(spec))
         } catch (t: Throwable) {
            log { "SpecExecutor: Error executing spec $t" }
            Result.failure(t)
         }
      }

      log { "SpecExecutor: Executing ${interceptors.size} spec interceptors" }
      return interceptors.foldRight(initial) { ext, fn ->
         { spec -> ext.intercept(spec, fn) }
      }.invoke(spec)
   }

   /**
    * Creates an instance of the given [SpecRef], notifies users of the instantiation event
    * or instantiation failure, and returns a Result with the error or spec.
    */
   private suspend fun createInstance(ref: SpecRef): Result<Spec> =
      ref.instance(conf.registry())
         .onFailure { extensions.specInstantiationError(ref.kclass, it) }
         .flatMap { spec -> extensions.specInstantiated(spec).map { spec } }
}

interface SpecExecutorDelegate {
   suspend fun execute(spec: Spec): Map<TestCase, TestResult>
}

internal expect fun createSpecExecutorDelegate(
   listener: TestEngineListener,
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   configuration: Configuration,
): SpecExecutorDelegate

