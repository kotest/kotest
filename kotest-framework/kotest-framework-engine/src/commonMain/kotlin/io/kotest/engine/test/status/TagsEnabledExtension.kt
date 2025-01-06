package io.kotest.engine.test.status

import io.kotest.core.TagExpression
import io.kotest.core.log
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.tags.isActive
import io.kotest.engine.tags.parse

/**
 * A [TestEnabledExtension] that uses [io.kotest.core.Tag]s.
 *
 * This extension disables a test if:
 *
 * - Excluded tags have been specified and this test has a [io.kotest.core.Tag] which is one of those excluded.
 * - Included tags have been specified and this test either has no tags,
 *   or does not have any of the specified inclusion tags.
 *
 *  Note: tags are attached to tests either through test config, or at the spec level.
 */
internal class TagsEnabledExtension(
   private val tags: TagExpression,
   private val testConfigResolver: TestConfigResolver,
) : TestEnabledExtension {
   override fun isEnabled(testCase: TestCase): Enabled {
      val enabledInTags = tags.parse().isActive(testConfigResolver.tags(testCase))
      if (!enabledInTags) {
         return Enabled
            .disabled("Disabled by tags: ${tags.expression}")
            .also { it.reason?.let { log { it } } }
      }
      return Enabled.enabled
   }
}
