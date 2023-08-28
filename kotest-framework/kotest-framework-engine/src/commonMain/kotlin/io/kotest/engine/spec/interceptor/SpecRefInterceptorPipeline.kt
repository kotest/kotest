package io.kotest.engine.spec.interceptor

import io.kotest.common.Platform
import io.kotest.common.platform
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.ref.ApplyExtensionsInterceptor
import io.kotest.engine.spec.interceptor.ref.BeforeSpecStateInterceptor
import io.kotest.engine.spec.interceptor.ref.EnabledIfInterceptor
import io.kotest.engine.spec.interceptor.ref.FinalizeSpecInterceptor
import io.kotest.engine.spec.interceptor.ref.IgnoredSpecInterceptor
import io.kotest.engine.spec.interceptor.ref.PrepareSpecInterceptor
import io.kotest.engine.spec.interceptor.ref.RequiresPlatformInterceptor
import io.kotest.engine.spec.interceptor.ref.RequiresTagInterceptor
import io.kotest.engine.spec.interceptor.ref.SpecFilterInterceptor
import io.kotest.engine.spec.interceptor.ref.SpecFinishedInterceptor
import io.kotest.engine.spec.interceptor.ref.SpecRefExtensionInterceptor
import io.kotest.engine.spec.interceptor.ref.SpecStartedInterceptor
import io.kotest.engine.spec.interceptor.ref.SystemPropertySpecFilterInterceptor
import io.kotest.engine.spec.interceptor.ref.TagsInterceptor
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName

internal class SpecRefInterceptorPipeline(
   private val context: EngineContext,
) {

   private val logger = Logger(SpecInterceptorPipeline::class)
   private val listener = context.listener
   private val configuration = context.configuration

   /**
    * Executes all [SpecRefInterceptor]s in turn, returning a result, which will be
    * succesful and contain the results of all tests, unless some setup callback
    * fails, in which case the result will be a failure.
    *
    * If any [SpecRefInterceptor] elects to skip the spec entirely, then the result will contain
    * an empty map.
    */
   suspend fun execute(
      ref: SpecRef,
      inner: suspend (SpecRef) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      val interceptors = createPipeline()
      logger.log { Pair(ref.kclass.bestName(), "Executing ${interceptors.size} reference interceptors") }
      return interceptors.foldRight(inner) { interceptor, fn: suspend (SpecRef) -> Result<Map<TestCase, TestResult>> ->
         { ref -> interceptor.intercept(ref, fn) }
      }.invoke(ref)
   }

   private fun createPipeline(): List<SpecRefInterceptor> {
      return listOfNotNull(
         RequiresPlatformInterceptor(listener, context, configuration.registry),
         if (platform == Platform.JVM) EnabledIfInterceptor(listener, configuration.registry) else null,
         IgnoredSpecInterceptor(listener, configuration.registry),
         SpecFilterInterceptor(listener, configuration.registry),
         SystemPropertySpecFilterInterceptor(listener, configuration.registry),
         TagsInterceptor(listener, configuration),
         if (platform == Platform.JVM) RequiresTagInterceptor(
            listener,
            configuration,
            configuration.registry
         ) else null,
         SpecRefExtensionInterceptor(configuration.registry),
         SpecStartedInterceptor(listener),
         SpecFinishedInterceptor(listener),
         if (platform == Platform.JVM) ApplyExtensionsInterceptor(configuration.registry) else null,
         PrepareSpecInterceptor(configuration.registry),
         BeforeSpecStateInterceptor(context),
         FinalizeSpecInterceptor(configuration.registry),
      )
   }
}
