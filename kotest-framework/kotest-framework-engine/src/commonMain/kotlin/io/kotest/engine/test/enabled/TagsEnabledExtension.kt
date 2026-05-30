package io.kotest.engine.test.enabled

import io.kotest.common.syspropOrEnv
import io.kotest.core.Logger
import io.kotest.core.descriptors.DescriptorPaths
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.tags.TagExpression
import io.kotest.engine.tags.isActive
import io.kotest.engine.tags.parse

/**
 * A [TestEnabledExtension] that uses [io.kotest.core.Tag]s.
 *
 * This extension disables a test if:
 *
 * - Excluded tags have been specified, and this test has a [io.kotest.core.Tag] which is one of those excluded.
 * - Included tags have been specified, and this test either has no tags
 *   or does not have any of the specified inclusion tags.
 *
 * Note: tags are attached to tests either through test config or at the spec level.
 *
 * **Data test ancestor path bypass**: when [KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH] is set
 * (by the IntelliJ plugin when running a specific data test nested inside regular containers), a container
 * with no explicit tags that fails the tag filter is still allowed to run if it is one of the containers
 * named in that path. i.e. it lies on the direct route from the spec root to the target data test.
 * Sibling containers at every level remain excluded by tag filtering.
 */
internal class TagsEnabledExtension(
   private val tags: TagExpression,
   private val testConfigResolver: TestConfigResolver,
) : TestEnabledExtension {

   private val logger = Logger<TagsEnabledExtension>()

   override fun isEnabled(testCase: TestCase): Enabled {
      if (syspropOrEnv(KotestEngineProperties.KOTEST_TEST_ENABLED_OVERRIDE) == "true") return Enabled.enabled
      val enabledInTags = tags.parse().isActive(testConfigResolver.tags(testCase))
      if (!enabledInTags) {
         // When the IDE plugin runs a specific data test nested inside a regular container, allow
         // only the direct ancestor containers (not siblings) to bypass tag filtering so they can
         // run and discover their data test children.
         val ancestorPath = syspropOrEnv(KotestEngineProperties.KOTEST_DATA_TEST_ANCESTOR_PATH)
         if (ancestorPath != null
            && testCase.type == TestType.Container
            && testCase.config?.tags.isNullOrEmpty()
         ) {
            val containerPath = testCase.descriptor.testParts().joinToString(DescriptorPaths.TEST_DELIMITER)
            if (ancestorPath == containerPath || ancestorPath.startsWith("$containerPath${DescriptorPaths.TEST_DELIMITER}")) {
               return Enabled.enabled
            }
         }
         return Enabled
            .disabled("Disabled by tags: ${tags.expression}")
            .also { it.reason?.let { logger.log { it } } }
      }
      return Enabled.enabled
   }

}
