package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.toProjectContext
import io.kotest.engine.spec.interceptor.instance.AfterSpecCallbackInterceptor
import io.kotest.engine.spec.interceptor.instance.BeforeSpecListenerSpecInterceptor
import io.kotest.engine.spec.interceptor.instance.ConfigurationInContextSpecInterceptor
import io.kotest.engine.spec.interceptor.instance.EngineContextInterceptor
import io.kotest.engine.spec.interceptor.instance.InlineTagSpecInterceptor
import io.kotest.engine.spec.interceptor.instance.ProjectContextInterceptor
import io.kotest.engine.spec.interceptor.instance.SpecExtensionInterceptor
import io.kotest.engine.specInterceptorsForPlatform
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName

/**
 * Executes [SpecInterceptor]s against a given spec instance.
 */
internal class SpecInterceptorPipeline(
   private val context: EngineContext,
) {

   private val logger = Logger(SpecInterceptorPipeline::class)
   private val listener = context.listener
   private val configuration = context.configuration

   /**
    * Executes all [SpecInterceptor]s in turn, returning a result, which will be
    * succesfull and contain the results of all tests, unless some setup callback
    * fails, in which case the result will be a failure.
    *
    * If any [SpecInterceptor] elects to skip the given spec instance, then the result will contain
    * an empty map.
    */
   suspend fun execute(
      spec: Spec,
      initial: suspend (Spec) -> Result<Map<TestCase, TestResult>>,
   ): Result<Map<TestCase, TestResult>> {
      val interceptors = createPipeline()
      logger.log { Pair(spec::class.bestName(), "Executing ${interceptors.size} spec interceptors") }
      return interceptors.foldRight(initial) { ext, fn -> { spec -> ext.intercept(spec, fn) } }.invoke(spec)
   }

   private fun createPipeline(): List<SpecInterceptor> {
      return listOf(
         EngineContextInterceptor(context),
         ProjectContextInterceptor(context.toProjectContext()),
         SpecExtensionInterceptor(configuration.registry),
         ConfigurationInContextSpecInterceptor(configuration),
         InlineTagSpecInterceptor(listener, configuration),
         BeforeSpecListenerSpecInterceptor(context),
         AfterSpecCallbackInterceptor(configuration.registry),
      ) + specInterceptorsForPlatform()
   }
}
