package io.kotest.engine.test.names

import io.kotest.common.KotestInternal
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.DisplayNameFormatterExtension
import io.kotest.core.test.TestCase
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.extensions.DefaultExtensionRegistry
import io.kotest.engine.names.DisplayNameFormatter
import kotlin.reflect.KClass

/**
 * The entry point for formatting spec and test names in Kotest.
 *
 * This implementation takes into account users custom formatting via any registered [DisplayNameFormatter]s.
 * If no custom formatters are registered, then this implementation will use the [DefaultDisplayNameFormatter].
 */
@KotestInternal
class DisplayNameFormatting(private val config: AbstractProjectConfig?) {

   private val customFormatter = config?.extensions
      ?.filterIsInstance<DisplayNameFormatterExtension>()
      ?.firstOrNull()
      ?.formatter()

   private val fallback = DefaultDisplayNameFormatter(
      ProjectConfigResolver(config = config, registry = DefaultExtensionRegistry()),
      TestConfigResolver(projectConfig = config, registry = DefaultExtensionRegistry())
   )

   fun format(kclass: KClass<*>): String {
      return customFormatter?.format(kclass) ?: fallback.format(kclass)
   }

   fun format(testCase: TestCase): String {
      return customFormatter?.format(testCase) ?: fallback.format(testCase)
   }

   fun formatTestPath(testCase: TestCase, separator: String): String {
      return when (val parent = testCase.parent) {
         null -> format(testCase)
         else -> formatTestPath(parent, separator) + separator + format(testCase)
      }
   }
}
