package io.kotest.engine.test.status

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.EnabledExtension
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.spec.SpecExtensionsExecutor
import io.kotest.engine.tags.runtimeTagExpression

/**
 * Returns [Enabled.enabled] if the given [TestCase] is enabled based on default rules
 * from [isEnabledInternal] or any registered [EnabledExtension]s.
 */
suspend fun TestCase.isEnabled(conf: ProjectConfiguration): Enabled {
   val internal = isEnabledInternal(conf)
   return if (!internal.isEnabled) {
      internal
   } else {
      val disabled = SpecExtensionsExecutor(conf.registry)
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
internal fun TestCase.isEnabledInternal(conf: ProjectConfiguration): Enabled {

   val extensions = listOf(
      TestConfigEnabledExtension,
      TagsEnabledExtension(conf.runtimeTagExpression()),
      TestFilterEnabledExtension(conf.registry),
      SystemPropertyTestFilterEnabledExtension,
      FocusEnabledExtension,
      BangTestEnabledExtension,
      SeverityLevelEnabledExtension,
   )

   return extensions.fold(Enabled.enabled) { acc, ext -> if (acc.isEnabled) ext.isEnabled(this) else acc }
}
