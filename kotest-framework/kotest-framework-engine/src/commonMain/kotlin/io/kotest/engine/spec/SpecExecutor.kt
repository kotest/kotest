package io.kotest.engine.spec

import io.kotest.common.KotestInternal
import io.kotest.core.Logger
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.flatMap
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptorPipeline
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * Executes a [SpecRef].
 *
 * First invokes the [SpecRef] against a [SpecRefInterceptorPipeline], then creates an instance
 * of the reference, then executes the spec instance via a [SpecExecutor2].
 */
internal class SpecExecutor(
   private val context: EngineContext,
) {

   @Suppress("DEPRECATION")
   private val logger = Logger(SpecExecutor::class)
   private val pipeline = SpecRefInterceptorPipeline(context)
   private val extensions = SpecExtensions(context.specConfigResolver, context.projectConfigResolver)

   suspend fun execute(kclass: KClass<out Spec>) {
      execute(SpecRef.Reference(kclass))
   }

   suspend fun execute(ref: SpecRef) {
      logger.log { Pair(ref.kclass.bestName(), "Received $ref") }
      val innerExecute = object : NextSpecRefInterceptor {
         override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
            return createInstance(ref).flatMap { executeInDelegate(it) }
         }
      }
      pipeline.execute(ref, innerExecute)
   }

   private suspend fun executeInDelegate(spec: Spec): Result<Map<TestCase, TestResult>> {
      return try {
         @Suppress("DEPRECATION")
         val executor = SpecExecutor2(context)
          executor.execute(spec)
      } catch (t: Throwable) {
         logger.log { Pair(spec::class.bestName(), "Error executing spec $t") }
         Result.failure(t)
      }
   }

   /**
    * Creates an instance of the given [SpecRef], notifies users of the instantiation event
    * or instantiation failure, and returns a Result with the error or spec.
    *
    * After this method is called the spec is sealed.
    */
   private suspend fun createInstance(ref: SpecRef): Result<Spec> =
      ref.instance(context.registry, context.projectConfigResolver)
         .onFailure { extensions.specInstantiationError(ref.kclass, it) }
         .flatMap { spec -> extensions.specInstantiated(spec).map { spec } }
         .onSuccess { if (it is DslDrivenSpec) it.seal() }
}

/**
 * Used to test a [SpecExecutor] from another module.
 * Should not be used by user's code and is subject to change.
 */
@KotestInternal
suspend fun testSpecExecutor(
   context: EngineContext,
   ref: SpecRef.Reference
) {
   SpecExecutor(context).execute(ref)
}
