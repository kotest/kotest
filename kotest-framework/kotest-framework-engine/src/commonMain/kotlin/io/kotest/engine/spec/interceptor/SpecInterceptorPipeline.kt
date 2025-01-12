package io.kotest.engine.spec.interceptor

import io.kotest.core.Logger
import io.kotest.core.Platform
import io.kotest.core.platform
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.interceptors.toProjectContext
import io.kotest.engine.spec.interceptor.instance.AfterSpecListenerInterceptor
import io.kotest.engine.spec.interceptor.instance.BeforeSpecFailureInterceptor
import io.kotest.engine.spec.interceptor.instance.CoroutineDispatcherFactorySpecInterceptor
import io.kotest.engine.spec.interceptor.instance.EngineContextInterceptor
import io.kotest.engine.spec.interceptor.instance.IgnoreNestedSpecStylesInterceptor
import io.kotest.engine.spec.interceptor.instance.InlineTagSpecInterceptor
import io.kotest.engine.spec.interceptor.instance.ProjectConfigResolverSpecInterceptor
import io.kotest.engine.spec.interceptor.instance.ProjectContextInterceptor
import io.kotest.engine.spec.interceptor.instance.SpecExtensionInterceptor
import io.kotest.mpp.bestName

/**
 * Executes [SpecInterceptor]s against a given spec instance.
 */
internal class SpecInterceptorPipeline(
   private val context: EngineContext,
) {

   private val logger = Logger(SpecInterceptorPipeline::class)
   private val listener = context.listener

   /**
    * Executes all [SpecInterceptor]s in turn, returning a result, which will be
    * successful and contain the results of all tests, unless some setup callback
    * fails, in which case the result will be a failure.
    *
    * If any [SpecInterceptor] elects to skip the given spec instance, then the result will contain
    * an empty map.
    */
   suspend fun execute(
      spec: Spec,
      context: SpecContext,
      initial: NextSpecInterceptor,
   ): Result<Map<TestCase, TestResult>> {
      val interceptors = createPipeline(context)
      logger.log { Pair(spec::class.bestName(), "Executing ${interceptors.size} spec interceptors") }
      return interceptors.foldRight(initial) { ext, next ->
         object : NextSpecInterceptor {
            override suspend fun invoke(spec: Spec): Result<Map<TestCase, TestResult>> {
               return ext.intercept(spec, next)
            }
         }
      }.invoke(spec)
   }

   private fun createPipeline(specContext: SpecContext): List<SpecInterceptor> {
      return listOfNotNull(
         if (platform == Platform.JS) IgnoreNestedSpecStylesInterceptor(listener, context.specExtensions()) else null,
         EngineContextInterceptor(this.context),
         ProjectConfigResolverSpecInterceptor(context.projectConfigResolver),
         // the dispatcher factory should run before before/after callbacks so they are executed in the right context
         CoroutineDispatcherFactorySpecInterceptor(context.specConfigResolver),
         ProjectContextInterceptor(this.context.toProjectContext()),
         SpecExtensionInterceptor(context.specExtensions()),
         InlineTagSpecInterceptor(listener, context.projectConfigResolver, context.specExtensions()),
         BeforeSpecFailureInterceptor(specContext),
         AfterSpecListenerInterceptor(specContext, context.specExtensions()),
      )
   }
}
