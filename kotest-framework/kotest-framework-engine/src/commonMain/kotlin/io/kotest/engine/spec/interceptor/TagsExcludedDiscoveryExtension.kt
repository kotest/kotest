package io.kotest.engine.spec.interceptor

import io.kotest.core.Tags
import io.kotest.core.config.Configuration
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.tags.activeTags
import io.kotest.engine.tags.isPotentiallyActive
import io.kotest.engine.tags.parse

/**
 * Filters any [Spec] that can be eagerly excluded based on the @[Tags] annotation at the class level.
 */
class TagsExcludedSpecInterceptor(
   private val listener: TestEngineListener,
   private val conf: Configuration,
) : SpecRefInterceptor {

   override suspend fun intercept(
      fn: suspend (SpecRef) -> Map<TestCase, TestResult>
   ): suspend (SpecRef) -> Map<TestCase, TestResult> = { ref ->
      val potentiallyActive = conf.activeTags().parse().isPotentiallyActive(ref.kclass)
      if (potentiallyActive) {
         fn(ref)
      } else {
         listener.specIgnored(ref.kclass)
         SpecExtensions(conf.registry()).ignored(ref.kclass)
         emptyMap()
      }
   }
}
