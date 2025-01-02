package io.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.extensions.SpecExecutionOrderExtension
import io.kotest.core.project.TestSuite
import io.kotest.engine.EngineResult
import io.kotest.engine.spec.DefaultSpecExecutionOrderExtension
import io.kotest.core.log

/**
 * An [EngineInterceptor] that sorts the [TestSuite] according to registered [SpecExecutionOrderExtension]s
 * or falling back to the [DefaultSpecExecutionOrderExtension].
 */
@KotestInternal
internal object SpecSortEngineInterceptor : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: NextEngineInterceptor
   ): EngineResult {

      // spec classes are ordered using SpecExecutionOrderExtension extensions
      val exts = context.configuration.registry.all().filterIsInstance<SpecExecutionOrderExtension>().ifEmpty {
         listOf(DefaultSpecExecutionOrderExtension(context.configuration.specExecutionOrder, context.configuration))
      }

      log { "SpecSortEngineInterceptor: Sorting specs using extensions $exts" }
      val specs = exts.fold(context.suite.specs) { acc, op -> op.sort(acc) }
      return execute(context.withTestSuite(TestSuite(specs)))
   }
}
