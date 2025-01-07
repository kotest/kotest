package io.kotest.engine.test.names

import io.kotest.common.KotestInternal
import io.kotest.core.extensions.DisplayNameFormatterExtension
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.TestConfigResolver

@KotestInternal
fun getFallbackDisplayNameFormatter(
   projectConfigResolver: ProjectConfigResolver,
   testConfigResolver: TestConfigResolver,
): FallbackDisplayNameFormatter {
   val custom = projectConfigResolver.extensions()
      .filterIsInstance<DisplayNameFormatterExtension>()
      .firstOrNull()?.formatter()
   return FallbackDisplayNameFormatter(custom, DefaultDisplayNameFormatter(projectConfigResolver, testConfigResolver))
}
