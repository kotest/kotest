package io.kotest.engine.test.enabled

import io.kotest.common.syspropOrEnv
import io.kotest.core.Logger
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
 * (by the IntelliJ plugin when running a specific data test nested inside regular containers), containers
 * with no explicit tags whose path-from-root is a prefix of — or exactly equals — that value are allowed
 * through despite failing the tag filter. This ensures the engine traverses the ancestor containers needed
 * to discover the target data test, without also running sibling containers at any level.
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
            val containerPath = buildContainerPath(testCase)
            if (ancestorPath == containerPath || ancestorPath.startsWith("$containerPath -- ")) {
               return Enabled.enabled
            }
         }
         return Enabled
            .disabled("Disabled by tags: ${tags.expression}")
            .also { it.reason?.let { logger.log { it } } }
      }
      return Enabled.enabled
   }

   /**
    * Builds a path string for [testCase] by walking up the [TestCase.parent] chain and joining
    * [io.kotest.core.names.TestName.name] values with `" -- "`.
    *
    * Raw names (no spec-style prefix/suffix such as `"Given: "`) are used intentionally so the result
    * matches the path the IntelliJ plugin builds from PSI source — which also extracts only the string
    * literal passed to the container function.
    *
    * Example: `context("child context")` nested inside `context("parent context")` → `"parent context -- child context"`.
    * TODO: this is a good candidate for the shared module between IJ plugin and Framework - as this code is done times and times again
    */
   private fun buildContainerPath(testCase: TestCase): String {
      val parts = mutableListOf<String>()
      var current: TestCase? = testCase
      while (current != null) {
         parts.add(0, current.name.name)
         current = current.parent
      }
      return parts.joinToString(" -- ")
   }
}
