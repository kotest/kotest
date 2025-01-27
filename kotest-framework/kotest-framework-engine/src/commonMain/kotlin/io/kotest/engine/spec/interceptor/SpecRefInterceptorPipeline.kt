package io.kotest.engine.spec.interceptor

import io.kotest.core.Logger
import io.kotest.core.Platform
import io.kotest.core.platform
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.ref.ApplyExtensionsInterceptor
import io.kotest.engine.spec.interceptor.ref.EnabledIfInterceptor
import io.kotest.engine.spec.interceptor.ref.FinalizeSpecInterceptor
import io.kotest.engine.spec.interceptor.ref.IgnoredSpecInterceptor
import io.kotest.engine.spec.interceptor.ref.PrepareSpecInterceptor
import io.kotest.engine.spec.interceptor.ref.RequiresPlatformInterceptor
import io.kotest.engine.spec.interceptor.ref.RequiresTagInterceptor
import io.kotest.engine.spec.interceptor.ref.DescriptorFilterSpecRefInterceptor
import io.kotest.engine.spec.interceptor.ref.SpecFinishedInterceptor
import io.kotest.engine.spec.interceptor.ref.SpecRefExtensionInterceptor
import io.kotest.engine.spec.interceptor.ref.SpecStartedInterceptor
import io.kotest.engine.spec.interceptor.ref.TagsInterceptor
import io.kotest.mpp.bestName

internal class SpecRefInterceptorPipeline(
   private val context: EngineContext,
) {

   private val logger = Logger(SpecInterceptorPipeline::class)
   private val listener = context.listener

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
      inner: NextSpecRefInterceptor,
   ): Result<Map<TestCase, TestResult>> {
      val interceptors = platformInterceptors(context) + createCommonInterceptors()
      logger.log { Pair(ref.kclass.bestName(), "Executing ${interceptors.size} reference interceptors") }
      return interceptors.foldRight(inner) { interceptor, fn ->
         object : NextSpecRefInterceptor {
            override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
               return interceptor.intercept(ref, fn)
            }
         }
      }.invoke(ref)
   }

   private fun createCommonInterceptors(): List<SpecRefInterceptor> {
      return listOfNotNull(
         RequiresPlatformInterceptor(listener, context),
         if (platform == Platform.JVM) EnabledIfInterceptor(listener, context.specExtensions()) else null,
         IgnoredSpecInterceptor(listener, context.specExtensions()),
         if (platform == Platform.JVM) ApplyExtensionsInterceptor(context.registry) else null,
         DescriptorFilterSpecRefInterceptor(listener, context.projectConfigResolver, context.specExtensions()),
//         SystemPropertyDescriptorFilterInterceptor(listener, context.specExtensions()),
         TagsInterceptor(listener, context.projectConfigResolver, context.specExtensions()),
         if (platform == Platform.JVM)
            RequiresTagInterceptor(listener, context.projectConfigResolver, context.specExtensions())
         else
            null,
         SpecRefExtensionInterceptor(context.projectConfigResolver),
         SpecStartedInterceptor(listener),
         SpecFinishedInterceptor(listener),
         PrepareSpecInterceptor(context.specExtensions()),
         FinalizeSpecInterceptor(context.specExtensions()),
      )
   }
}

internal expect fun platformInterceptors(context: EngineContext): List<SpecRefInterceptor>
