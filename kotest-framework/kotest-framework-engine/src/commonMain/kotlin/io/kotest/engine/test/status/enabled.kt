package io.kotest.engine.test.status

import io.kotest.core.config.Configuration
import io.kotest.core.extensions.EnabledExtension
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.tags.activeTags

/**
 * Returns [Enabled.isEnabled] if the given [TestCase] is enabled based on default rules
 * from [isEnabledInternal] or any registered [EnabledExtension]s.
 */
suspend fun TestCase.isEnabled(conf: Configuration): Enabled {
   val internal = isEnabledInternal(conf)
   return if (!internal.isEnabled) {
      internal
   } else {
      SpecExtensions(conf.registry())
         .extensions(spec)
         .filterIsInstance<EnabledExtension>()
         .map { it.isEnabled(descriptor) }
         .let { Enabled.fold(it) }
   }
}

/**
 * Determines enabled status by using [TestEnabledExtension]s.
 */
internal fun TestCase.isEnabledInternal(conf: Configuration): Enabled {

   val extensions = listOf(
      TestConfigEnabledExtension,
      TagsEnabledExtension(conf.activeTags()),
      TestFilterEnabledExtension(conf.registry()),
      FocusEnabledExtension,
      BangTestEnabledExtension,
      SeverityLevelEnabledExtension,
   )

   return extensions.fold(Enabled.enabled) { acc, ext -> if (acc.isEnabled) ext.isEnabled(this) else acc }
}
