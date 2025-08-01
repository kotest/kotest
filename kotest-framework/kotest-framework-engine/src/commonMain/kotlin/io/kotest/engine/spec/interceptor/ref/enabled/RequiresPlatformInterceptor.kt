package io.kotest.engine.spec.interceptor.ref.enabled

import io.kotest.core.annotation.RequiresPlatform
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.engine.flatMap
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.common.reflection.annotation

/**
 * A [io.kotest.engine.spec.interceptor.SpecRefInterceptor] which will ignore specs if they are annotated with @[io.kotest.core.annotation.RequiresPlatform]
 * and the engine is not executing on that platform.
 */
internal class RequiresPlatformInterceptor(
   private val listener: TestEngineListener,
   private val context: EngineContext,
) : SpecRefInterceptor {

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {
      return when (val requiresPlatform = ref.kclass.annotation<RequiresPlatform>()) {
         null -> next.invoke(ref)
         else -> {
            if (requiresPlatform.values.contains(context.platform)) next.invoke(ref)
            else runCatching { listener.specIgnored(ref.kclass, "Disabled by @RequiresPlatform") }
               .flatMap { context.specExtensions().ignored(ref.kclass, "Disabled by @RequiresPlatform") }
               .map { emptyMap() }
         }
      }
   }
}
