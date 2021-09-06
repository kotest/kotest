package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener

class SpecEnterExitInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {

   override suspend fun intercept(
      fn: suspend (SpecRef) -> Map<TestCase, TestResult>
   ): suspend (SpecRef) -> Map<TestCase, TestResult> = { ref ->
      runCatching {
         listener.specEnter(ref.kclass)
         fn(ref)
      }.onSuccess {
         listener.specExit(ref.kclass, null)
      }.onFailure {
         listener.specExit(ref.kclass, it)
      }.getOrElse { emptyMap() }
   }
}
