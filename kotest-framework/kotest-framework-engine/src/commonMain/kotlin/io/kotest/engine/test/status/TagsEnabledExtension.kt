package io.kotest.engine.test.status

import io.kotest.core.config.configuration
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.tags.activeTags
import io.kotest.engine.tags.isActive
import io.kotest.engine.tags.parse
import io.kotest.mpp.log

/**
 * A [TestEnabledExtension] that uses [Tag]s.
 *
 * This extension disables a test if:
 *
 * - Excluded tags have been specified and this test has a [Tag] which is one of those excluded
 * - Included tags have been specified and this test either has no tags,
 *   or does not have any of the specified inclusion tags.
 *
 *  Note: tags are defined either through [TestCaseConfig] or in the [Spec] dsl.
 */
object TagsEnabledExtension : TestEnabledExtension {
   override fun isEnabled(testCase: TestCase): Enabled {
      val enabledInTags = configuration.activeTags().parse().isActive(testCase.tags())
      if (!enabledInTags) {
         return Enabled.disabled("${testCase.descriptor.name.testName} is disabled by tags")
            .also { log { it.reason } }
      }
      return Enabled.enabled
   }
}
