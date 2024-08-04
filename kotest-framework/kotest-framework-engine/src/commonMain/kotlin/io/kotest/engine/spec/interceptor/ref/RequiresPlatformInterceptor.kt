package io.kotest.engine.spec.interceptor.ref

import io.kotest.engine.flatMap
import io.kotest.core.annotation.RequiresPlatform
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensionsExecutor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.mpp.annotation

/**
 * A [SpecRefInterceptor] which will ignore specs if they are annotated with @[RequiresPlatform]
 * and the engine is not executing on that platform.
 */
internal class RequiresPlatformInterceptor(
   private val listener: TestEngineListener,
   private val context: EngineContext,
   registry: ExtensionRegistry,
) : SpecRefInterceptor {

   private val extensions = SpecExtensionsExecutor(registry)

   override suspend fun intercept(
      ref: SpecRef,
      fn: suspend (SpecRef) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return when (val requiresPlatform = ref.kclass.annotation<RequiresPlatform>()) {
         null -> fn(ref)
         else -> {
            if (requiresPlatform.values.contains(context.platform)) fn(ref)
            else runCatching { listener.specIgnored(ref.kclass, "Disabled by @RequiresPlatform") }
               .flatMap { extensions.ignored(ref.kclass, "Disabled by @RequiresPlatform") }
               .map { emptyMap() }
         }
      }
   }
}
