package io.kotest.engine.interceptors

import io.kotest.common.DelicateKotest
import io.kotest.common.KotestInternal
import io.kotest.core.extensions.EngineContextExtension
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite

@OptIn(KotestInternal::class)
internal object EngineContextExtensionEngineInterceptor : EngineInterceptor {

   @OptIn(DelicateKotest::class)
   override suspend fun intercept(
      context: EngineContext,
      execute: suspend (EngineContext) -> EngineResult
   ): EngineResult {

      val input = EngineContextExtension.EngineContext(context.suite.specs, context.configuration, context.tags)
      val output = context.configuration.extensions()
         .filterIsInstance<EngineContextExtension>()
         .fold(input) { acc, op -> op.getContext(acc) }

      val result = EngineContext(
         TestSuite(output.specs),
         context.listener,
         output.tags,
         output.configuration
      )
      return execute(result)
   }
}
