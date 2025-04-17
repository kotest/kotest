package io.kotest.engine.spec.interceptor.ref

import io.kotest.core.Logger
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.extensions.DescriptorFilter
import io.kotest.engine.extensions.DescriptorFilterResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.mpp.bestName

/**
 * Evaluates a spec against any registered [DescriptorFilter]s.
 */
internal class DescriptorFilterSpecRefInterceptor(
   private val listener: TestEngineListener,
   private val projectConfigResolver: ProjectConfigResolver,
   private val specExtensions: SpecExtensions,
) : SpecRefInterceptor {

   private val logger = Logger(DescriptorFilterSpecRefInterceptor::class)

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {

      val filters = projectConfigResolver.extensions().filterIsInstance<DescriptorFilter>()
      logger.log { Pair(ref.kclass.bestName(), "${filters.size} descriptor filters") }

      val excluded = filters.firstNotNullOfOrNull {
         val result = it.filter(ref.kclass.toDescriptor())
         result as? DescriptorFilterResult.Exclude
      }
      logger.log { Pair(ref.kclass.bestName(), "excludedByFilters == $excluded") }

      return if (excluded == null) {
         next.invoke(ref)
      } else {
         val reason = excluded.reason ?: "Disabled by descriptor filter"
         listener.specIgnored(ref.kclass, reason)
         specExtensions.ignored(ref.kclass, reason)
         Result.success(emptyMap())
      }
   }
}
