package io.kotest.engine.spec.interceptor

import io.kotest.common.flatMap
import io.kotest.core.TagExpression
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.tags.isPotentiallyActive
import io.kotest.engine.tags.parse
import io.kotest.engine.tags.runtimeTags

/**
 * Filters any [Spec] that can be eagerly excluded based on the @[TagExpression] annotation at the class level.
 */
class TagsExcludedSpecInterceptor(
   private val listener: TestEngineListener,
   private val conf: ProjectConfiguration,
) : SpecRefInterceptor {

   private val extensions = SpecExtensions(conf.registry)

   override suspend fun intercept(
      ref: SpecRefContainer,
      fn: suspend (SpecRefContainer) -> Result<Pair<SpecRefContainer, Map<TestCase, TestResult>>>
   ): Result<Pair<SpecRefContainer, Map<TestCase, TestResult>>> {
      val potentiallyActive = conf.runtimeTags().parse().isPotentiallyActive(ref.specRef.kclass)
      return if (potentiallyActive) {
         fn(ref)
      } else {
         runCatching { listener.specIgnored(ref.specRef.kclass, null) }
            .flatMap { extensions.ignored(ref.specRef.kclass, "Skipped by tags") }
            .map { ref.disable() to emptyMap() }
      }
   }
}
