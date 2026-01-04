package io.kotest.engine.test.enabled

import io.kotest.core.extensions.EnabledExtension
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.tags.TagExpressionBuilder

/**
 * Checks if a [TestCase] is enabled or disabled before it is executed based on config, tags, annotations
 * and other runtime variables applied to the test case itself.
 */
internal class TestEnabledChecker(
   projectConfigResolver: ProjectConfigResolver,
   private val specConfigResolver: SpecConfigResolver,
   testConfigResolver: TestConfigResolver,
) {

   val extensions = listOf(
      TestConfigEnabledExtension(testConfigResolver),
      TagsEnabledExtension(TagExpressionBuilder.build(projectConfigResolver), testConfigResolver),
      DescriptorFilterTestEnabledExtension(projectConfigResolver),
      SystemPropertyTestFilterEnabledExtension,
      FocusEnabledExtension,
      BangTestEnabledExtension,
      SeverityLevelEnabledExtension(projectConfigResolver, testConfigResolver),
   )

   /**
    * Returns the [Enabled] status of the given [TestCase] based on default rules
    * from internal [TestEnabledExtension] or any registered user [EnabledExtension]s.
    */
   suspend fun isEnabled(testCase: TestCase): Enabled {
      val isEnabledViaInternalExtensions = isEnabledInternal(testCase)
      return if (isEnabledViaInternalExtensions.isDisabled) {
         isEnabledViaInternalExtensions
      } else {
         isEnabledUserExtensions(testCase)
      }
   }

   /**
    * Determines enabled status by using public [EnabledExtension]s.
    */
   private suspend fun isEnabledUserExtensions(testCase: TestCase): Enabled {
      return try {
         val disabled = specConfigResolver
            .extensions(testCase.spec)
            .filterIsInstance<EnabledExtension>()
            .map { it.isEnabled(testCase.descriptor) }
            .firstOrNull { it.isDisabled }
         disabled ?: Enabled.enabled
      } catch (t: Throwable) {
         return Enabled.disabled("Error during EnabledExtension: ${t.message}")
      }
   }

   /**
    * Determines enabled status by using internal [TestEnabledExtension]s.
    */
   fun isEnabledInternal(testCase: TestCase): Enabled {
      return try {
         extensions.forEach {
            val enabled = it.isEnabled(testCase)
            if (enabled.isDisabled) return enabled
         }
         Enabled.enabled
      } catch (t: Throwable) {
         return Enabled.disabled("Error during enabled check: ${t.message}")
      }
   }
}


