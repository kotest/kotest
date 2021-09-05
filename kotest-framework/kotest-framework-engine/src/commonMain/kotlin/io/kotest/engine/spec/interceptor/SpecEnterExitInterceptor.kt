package io.kotest.engine.spec.interceptor

import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.spec.SpecRef

class SpecEnterExitInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {
   override suspend fun intercept(fn: suspend (SpecRef) -> Unit): suspend (SpecRef) -> Unit = { ref ->
      listener.specEnter(ref.kclass)
      fn(ref)
      listener.specExit(ref.kclass)
   }
}
