package io.kotest.engine.spec.interceptor.ref

import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.flatMap
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.engine.tags.TagExpressionBuilder
import io.kotest.engine.tags.TagExpressionResult
import io.kotest.engine.tags.isPotentiallyActive
import io.kotest.engine.tags.parse

/**
 * Filters any [Spec] that can be eagerly excluded based on the @[io.kotest.core.annotation.Tags]
 * annotation at the class level.
 */
internal class TagsInterceptor(
   private val listener: TestEngineListener,
   private val projectConfigResolver: ProjectConfigResolver,
   private val specExtensions: SpecExtensions,
) : SpecRefInterceptor {

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {
      val potentiallyActive = TagExpressionResult.Exclude != TagExpressionBuilder.build(projectConfigResolver)
         .parse()
         .isPotentiallyActive(ref.kclass, projectConfigResolver)

      return if (potentiallyActive) {
         next.invoke(ref)
      } else {
         runCatching { listener.specIgnored(ref.kclass, null) }
            .flatMap { specExtensions.ignored(ref.kclass, "Skipped by tags") }
            .map { emptyMap() }
      }
   }
}
