package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.Spec
import io.kotest.engine.listener.TestEngineListener

class SpecStartedFinishedInterceptor(private val listener: TestEngineListener) : SpecExecutionInterceptor {

   override suspend fun intercept(fn: suspend (Spec) -> Unit): suspend (Spec) -> Unit = { spec ->
      listener.specStarted(spec::class)
      fn(spec)
      listener.specFinished(spec::class, null, emptyMap())
   }

}
