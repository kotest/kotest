package io.kotest.engine.test.names

import io.kotest.common.KotestInternal
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.DisplayNameFormatterExtension

@KotestInternal
fun getFallbackDisplayNameFormatter(
   registry: ExtensionRegistry,
   configuration: ProjectConfiguration,
): FallbackDisplayNameFormatter {
   val custom = registry.all()
      .filterIsInstance<DisplayNameFormatterExtension>()
      .firstOrNull()?.formatter()
   return FallbackDisplayNameFormatter(custom, DefaultDisplayNameFormatter(configuration))
}
