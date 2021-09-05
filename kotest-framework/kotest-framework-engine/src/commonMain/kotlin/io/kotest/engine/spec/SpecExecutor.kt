package io.kotest.engine.spec

import io.kotest.core.config.configuration
import io.kotest.core.extensions.SpecInterceptExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.engine.events.SpecExtensions
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.interceptor.InactiveSpecInterceptor
import io.kotest.engine.spec.interceptor.RunIfActiveTestsInterceptor
import io.kotest.engine.spec.interceptor.SpecInterceptExtensionsInterceptor
import io.kotest.engine.spec.interceptor.SpecPrepareFinalizeInterceptor
import io.kotest.engine.spec.interceptor.SpecStartedFinishedInterceptor
import io.kotest.fp.flatMap
import io.kotest.mpp.log

/**
 * Executes a single [SpecRef].
 *
 * Uses a [TestEngineListener] to notify of events in the spec lifecycle.
 *
 * The spec executor has two levels of interceptors:
 * [io.kotest.engine.spec.interceptor.SpecRefInterceptor] are executed before the spec is created.
 * [io.kotest.engine.spec.interceptor.SpecExecutionInterceptor] are executed after the spec is created.
 *
 */
class SpecExecutor(private val listener: TestEngineListener) {

   private val extensions = SpecExtensions(configuration)

   suspend fun execute(ref: SpecRef) {
      log { "SpecExecutor: Received $ref" }
      referenceInterceptors(ref)
   }

   private suspend fun referenceInterceptors(ref: SpecRef) {

      val interceptors = listOf(
         SpecPrepareFinalizeInterceptor(listener),
         InactiveSpecInterceptor(listener),
      )

      val innerExecute: suspend (SpecRef) -> Unit = {
         createInstance(ref)
            .map { specInterceptors(it) }
      }

      log { "SpecExecutor: Executing ${interceptors.size} reference interceptors" }
      interceptors.foldRight(innerExecute) { ext, fn ->
         { r -> ext.intercept(fn)(r) }
      }.invoke(ref)
   }

   private suspend fun specInterceptors(spec: Spec) {

      val interceptors = listOf(
         SpecInterceptExtensionsInterceptor(
            extensions.extensions(spec).filterIsInstance<SpecInterceptExtension>()
         ),
         RunIfActiveTestsInterceptor(listener),
         SpecStartedFinishedInterceptor(listener),
      )

      val innerExecute: suspend (Spec) -> Unit = {
         val delegate = createSpecExecutorDelegate(listener)
         log { "SpecExecutor: Created spec executor delegate $delegate" }
         delegate.execute(spec)
      }

      log { "SpecExecutor: Executing ${interceptors.size} spec interceptors" }
      interceptors.foldRight(innerExecute) { ext, fn ->
         { r -> ext.intercept(fn)(r) }
      }.invoke(spec)
   }

   /**
    * Creates an instance of the given [SpecRef], and notifies of the instantiation event
    * or error events.
    */
   private suspend fun createInstance(ref: SpecRef): Result<Spec> =
      ref.instance()
         .onFailure {
            listener.specInstantiationError(ref::class, it)
            log { "SpecExecutor: instantiation error for ${ref.kclass} $it" }
            it.printStackTrace()
         }
         .flatMap { spec -> extensions.specInitialize(spec).map { spec } }
         .flatMap { spec -> extensions.specInstantiated(spec).map { spec } }
}

interface SpecExecutorDelegate {
   suspend fun execute(spec: Spec)
}

expect fun createSpecExecutorDelegate(listener: TestEngineListener): SpecExecutorDelegate

