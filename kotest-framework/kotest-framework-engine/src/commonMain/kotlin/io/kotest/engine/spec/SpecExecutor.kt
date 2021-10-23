package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.common.Platform
import io.kotest.common.platform
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.Configuration
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.interceptor.ApplyExtensionsInterceptor
import io.kotest.engine.spec.interceptor.EnabledIfSpecInterceptor
import io.kotest.engine.spec.interceptor.FilteredSpecInterceptor
import io.kotest.engine.spec.interceptor.IgnoreNestedSpecStylesInterceptor
import io.kotest.engine.spec.interceptor.IgnoredSpecInterceptor
import io.kotest.engine.spec.interceptor.RunIfActiveInterceptor
import io.kotest.engine.spec.interceptor.SpecEnterInterceptor
import io.kotest.engine.spec.interceptor.SpecExitInterceptor
import io.kotest.engine.spec.interceptor.SpecExtensionInterceptor
import io.kotest.engine.spec.interceptor.SpecRefExtensionInterceptor
import io.kotest.engine.spec.interceptor.SpecStartedFinishedInterceptor
import io.kotest.engine.spec.interceptor.TagsExcludedSpecInterceptor
import io.kotest.fp.flatMap
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
         SpecExitInterceptor(listener),
         SpecEnterInterceptor(listener),
         EnabledIfSpecInterceptor(listener, conf.registry()),
         IgnoredSpecInterceptor(listener, conf.registry()),
         FilteredSpecInterceptor(listener, conf.registry()),
         TagsExcludedSpecInterceptor(listener, conf),
         SpecRefExtensionInterceptor(conf.registry()),
         ApplyExtensionsInterceptor(conf.registry()),
         PrepareSpecInterceptor(conf.registry()),
      )

      val innerExecute: suspend (SpecRef) -> Map<TestCase, TestResult> = {
         val spec = createInstance(ref)
            .onFailure { listener.specAborted(ref.kclass, it) }
            .getOrThrow()
         specInterceptors(spec)
      }

      log { "SpecExecutor: Executing ${interceptors.size} reference interceptors" }
      interceptors.foldRight(innerExecute) { ext, fn ->
         { r -> ext.intercept(fn)(r) }
      }.invoke(ref)
   }

   private suspend fun specInterceptors(spec: Spec): Map<TestCase, TestResult> {

      val interceptors = listOfNotNull(
         if (platform == Platform.JS) IgnoreNestedSpecStylesInterceptor(listener, conf.registry()) else null,
         SpecExtensionInterceptor(conf.registry()),
         RunIfActiveInterceptor(listener, conf),
         SpecStartedFinishedInterceptor(listener, conf.registry()),
      )

      val initial: suspend (Spec) -> Map<TestCase, TestResult> = {
         try {
            val delegate = createSpecExecutorDelegate(listener, defaultCoroutineDispatcherFactory, conf)
            log { "SpecExecutor: delegate=$delegate" }
            delegate.execute(spec)
         } catch (t: Throwable) {
            log { "SpecExecutor: Error executing spec $t" }
            throw t
         }
      }

      log { "SpecExecutor: Executing ${interceptors.size} spec interceptors" }
      return interceptors.foldRight(initial) { ext, fn ->
         { r -> ext.intercept(fn)(r) }
      }.invoke(spec)
   }

   /**
    * Creates an instance of the given [SpecRef], and notifies of the instantiation event
    * or error events.
    */
   private suspend fun createInstance(ref: SpecRef): Result<Spec> =
      ref.instance(conf.registry())
         .onFailure {
            log { "SpecExecutor: instantiation error for ${ref.kclass} $it" }
            listener.specInstantiationError(ref.kclass, it)
            extensions.specInstantiationError(ref.kclass, it)
         }
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

