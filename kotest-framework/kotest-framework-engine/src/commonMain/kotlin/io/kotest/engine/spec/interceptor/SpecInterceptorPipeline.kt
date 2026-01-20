package io.kotest.engine.spec.interceptor

import io.kotest.common.reflection.bestName
import io.kotest.core.Logger
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.TestEngineContext
import io.kotest.engine.toProjectContext
import io.kotest.engine.spec.interceptor.instance.BeforeAfterSpecCallbacksInterceptor
import io.kotest.engine.spec.interceptor.instance.CoroutineDispatcherFactorySpecInterceptor
import io.kotest.engine.spec.interceptor.instance.CoroutineScopeInterceptor
import io.kotest.engine.spec.interceptor.instance.EnabledTestsCheckSpecInterceptor
import io.kotest.engine.spec.interceptor.instance.EngineContextInterceptor
import io.kotest.engine.spec.interceptor.instance.InlineTagSpecInterceptor
import io.kotest.engine.spec.interceptor.instance.ProjectConfigResolverSpecInterceptor
import io.kotest.engine.spec.interceptor.instance.ProjectContextInterceptor
import io.kotest.engine.spec.interceptor.instance.SpecExtensionInterceptor
import io.kotest.engine.test.TestResult

/**
 * Executes [SpecInterceptor]s against a given spec instance.
 */
internal class SpecInterceptorPipeline(
   private val context: TestEngineContext,
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
   @Suppress("ObjectLiteralToLambda")
   suspend fun execute(
      spec: Spec,
      ref: SpecRef,
      initial: NextSpecInterceptor,
   ): Result<Map<TestCase, TestResult>> {
      val interceptors = createPipeline()
      logger.log { Pair(spec::class.bestName(), "Executing ${interceptors.size} spec interceptors") }
      return interceptors.foldRight(initial) { ext, next ->
         // changing this to a lambda seems to keep wrapping a result in a result, unsure why
         object : NextSpecInterceptor {
            override suspend fun invoke(spec: Spec): Result<Map<TestCase, TestResult>> {
               return ext.intercept(spec, ref, next)
            }
         }
      }.invoke(spec)
   }

   private fun createPipeline(): List<SpecInterceptor> {
      return listOfNotNull(
         CoroutineScopeInterceptor,
         EngineContextInterceptor(this.context),
         ProjectConfigResolverSpecInterceptor(context.projectConfigResolver),
         // the dispatcher factory should run before the before/after callbacks, so they are executed in the right context
         CoroutineDispatcherFactorySpecInterceptor(context.specConfigResolver),
         ProjectContextInterceptor(this.context.toProjectContext()),
         SpecExtensionInterceptor(context.specExtensions()),
         InlineTagSpecInterceptor(listener, context.projectConfigResolver, context.specExtensions()),
         EnabledTestsCheckSpecInterceptor(this.context),
         BeforeAfterSpecCallbacksInterceptor(context.specExtensions()),
      )
   }
}
