package io.kotest.engine.spec.interceptor.ref

import io.kotest.core.Logger
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.SpecFilterResult
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.mpp.bestName

/**
 * Evaluates a spec against any registered [SpecFilter]s.
 */
internal class SpecFilterInterceptor(
   private val listener: TestEngineListener,
   private val projectConfigResolver: ProjectConfigResolver,
   private val specExtensions: SpecExtensions,
) : SpecRefInterceptor {

   private val logger = Logger(SpecFilterInterceptor::class)

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {

      val excluded = projectConfigResolver.extensions().filterIsInstance<SpecFilter>().firstNotNullOfOrNull {
         val result = it.filter(ref.kclass)
         result as? SpecFilterResult.Exclude
      }
      logger.log { Pair(ref.kclass.bestName(), "excludedByFilters == $excluded") }

      return if (excluded == null) {
         next.invoke(ref)
      } else {
         val reason = excluded.reason ?: "Disabled by spec filter"
         listener.specIgnored(ref.kclass, reason)
         specExtensions.ignored(ref.kclass, reason)
         Result.success(emptyMap())
      }
   }
}
