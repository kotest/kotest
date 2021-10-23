package io.kotest.engine.spec

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.interceptor.SpecRefInterceptor

class PrepareSpecInterceptor(private val registry: ExtensionRegistry) : SpecRefInterceptor {

   override suspend fun intercept(
      fn: suspend (SpecRef) -> Map<TestCase, TestResult>
   ): suspend (SpecRef) -> Map<TestCase, TestResult> = { ref ->
      registry.all().filterIsInstance<PrepareSpecListener>().forEach {
         runCatching {
            it.prepareSpec(ref.kclass)
         }
      }
      fn(ref)
   }
}
