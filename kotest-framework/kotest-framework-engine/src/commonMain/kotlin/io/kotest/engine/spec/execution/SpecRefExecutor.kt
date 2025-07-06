package io.kotest.engine.spec.execution

import io.kotest.common.KotestInternal
import io.kotest.core.Logger
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.flatMap
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.SpecRefInflator
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptorPipeline
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * Executes a [SpecRef].
 *
 * First invokes the [SpecRef] against a [SpecRefInterceptorPipeline], then creates an instance
 * of the reference, then executes the spec.
 */
internal class SpecRefExecutor(
   private val context: EngineContext,
) {

   private val logger = Logger(SpecRefExecutor::class)
   private val pipeline = SpecRefInterceptorPipeline(context)
   private val extensions = SpecExtensions(context.specConfigResolver, context.projectConfigResolver)

   private val inflator = SpecRefInflator(
      registry = context.registry,
      projectConfigRegistry = context.projectConfigResolver,
      extensions = extensions,
   )

   suspend fun execute(kclass: KClass<out Spec>) {
      execute(SpecRef.Reference(kclass))
   }

   suspend fun execute(ref: SpecRef) {
      logger.log { Pair(ref.kclass.bestName(), "Received SpecRef $ref") }
      pipeline.execute(ref, object : NextSpecRefInterceptor {
         override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
            return innerExecute(ref)
         }
      })
   }

   /**
    * If we reach the end of the pipeline, we need to inflate the spec reference to see what
    * isolation mode it is using, and then execute the spec using the appropriate [SpecExecutor].
    *
    * All other actions on the spec instance level are done by the [SpecExecutor] implementations,
    * including running the spec instance pipeline.
    */
   private suspend fun innerExecute(ref: SpecRef): Result<Map<TestCase, TestResult>> {
      return inflator.inflate(ref).flatMap { spec ->
         try {
            when (context.specConfigResolver.isolationMode(spec)) {
               IsolationMode.SingleInstance -> SingleInstanceSpecExecutor(context).execute(ref, spec)
               IsolationMode.InstancePerRoot -> InstancePerRootSpecExecutor(context).execute(ref, spec)
               IsolationMode.InstancePerLeaf -> InstancePerLeafExecutor(context).execute(ref, spec)
               IsolationMode.InstancePerTest -> InstancePerTestExecutor(context).execute(ref, spec)
            }
         } catch (t: Throwable) {
            logger.log { Pair(spec::class.bestName(), "Error executing SpecRef $t") }
            Result.failure(t)
         }
      }
   }
}

/**
 * Used to test a [SpecRefExecutor] from another module.
 * Should not be used by user's code and is subject to change.
 */
@KotestInternal
suspend fun testSpecExecutor(
   context: EngineContext,
   ref: SpecRef.Reference
) {
   SpecRefExecutor(context).execute(ref)
}
