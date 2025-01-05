package io.kotest.engine.spec.interceptor.ref

import io.kotest.engine.config.ExtensionRegistry
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.SpecFilterResult
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.core.Logger
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.mpp.bestName

/**
 * Evaluates a spec against any registered [SpecFilter]s.
 */
internal class SpecFilterInterceptor(
   private val listener: TestEngineListener,
   private val registry: ExtensionRegistry
) : SpecRefInterceptor {

   private val extensions = SpecExtensions(registry)
   private val logger = Logger(SpecFilterInterceptor::class)

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {

      val excluded = registry.all().filterIsInstance<SpecFilter>().mapNotNull {
         val result = it.filter(ref.kclass)
         if (result is SpecFilterResult.Exclude) result else null
      }.firstOrNull()
      logger.log { Pair(ref.kclass.bestName(), "excludedByFilters == $excluded") }

      return if (excluded == null) {
         next.invoke(ref)
      } else {
         val reason = excluded.reason ?: "Disabled by spec filter"
         listener.specIgnored(ref.kclass, reason)
         extensions.ignored(ref.kclass, reason)
         Result.success(emptyMap())
      }
   }
}
