package io.kotest.engine.test.status

import io.kotest.core.extensions.EnabledExtension
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.tags.TagExpressionBuilder

/**
 * Returns [Enabled.enabled] if the given [TestCase] is enabled based on default rules
 * from [isEnabledInternal] or any registered [EnabledExtension]s.
 */
internal suspend fun TestCase.isEnabled(
   projectConfigResolver: ProjectConfigResolver,
   specConfigResolver: SpecConfigResolver,
   testConfigResolver: TestConfigResolver,
): Enabled {
   val internal = isEnabledInternal(projectConfigResolver, testConfigResolver)
   return if (!internal.isEnabled) {
      internal
   } else {
      val disabled = specConfigResolver
         .extensions(spec)
         .filterIsInstance<EnabledExtension>()
         .map { it.isEnabled(descriptor) }
         .firstOrNull { it.isDisabled }
      disabled ?: Enabled.enabled
   }
}

/**
 * Determines enabled status by using [TestEnabledExtension]s.
 */
internal fun TestCase.isEnabledInternal(
   projectConfigResolver: ProjectConfigResolver,
   testConfigResolver: TestConfigResolver
): Enabled {

   val extensions = listOf(
      TestConfigEnabledExtension(testConfigResolver),
      TagsEnabledExtension(TagExpressionBuilder.build(projectConfigResolver), testConfigResolver),
      TestFilterEnabledExtension(projectConfigResolver),
      SystemPropertyTestFilterEnabledExtension,
      FocusEnabledExtension,
      BangTestEnabledExtension,
      SeverityLevelEnabledExtension(projectConfigResolver, testConfigResolver),
   )

   return extensions.fold(Enabled.enabled) { acc, ext -> if (acc.isEnabled) ext.isEnabled(this) else acc }
}
