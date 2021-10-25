package io.kotest.engine.test.status

import io.kotest.core.Tags
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.tags.isActive
import io.kotest.engine.tags.parse
import io.kotest.mpp.log

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
internal class TagsEnabledExtension(private val tags: Tags) : TestEnabledExtension {
   override fun isEnabled(testCase: TestCase): Enabled {
      val enabledInTags = tags.parse().isActive(testCase.config.tags)
      if (!enabledInTags) {
         return Enabled
            .disabled("${testCase.descriptor.path()} is disabled by tags")
            .also { it.reason?.let { log { it } } }
      }
      return Enabled.enabled
   }
}
