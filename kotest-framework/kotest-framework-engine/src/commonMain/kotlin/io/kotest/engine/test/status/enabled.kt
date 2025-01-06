package io.kotest.engine.test.status

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.EnabledExtension
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.tags.runtimeTagExpression

/**
 * Returns [Enabled.enabled] if the given [TestCase] is enabled based on default rules
 * from [isEnabledInternal] or any registered [EnabledExtension]s.
 */
internal suspend fun TestCase.isEnabled(testConfigResolver: TestConfigResolver): Enabled {
   val internal = isEnabledInternal(testConfigResolver)
   return if (!internal.isEnabled) {
      internal
   } else {
      val disabled = SpecExtensions(conf.registry)
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
internal fun TestCase.isEnabledInternal(testConfigResolver: TestConfigResolver): Enabled {

   val testConfigResolver = TestConfigResolver(conf)

   val extensions = listOf(
      TestConfigEnabledExtension(testConfigResolver),
      TagsEnabledExtension(conf.runtimeTagExpression(), testConfigResolver),
      TestFilterEnabledExtension(conf.registry),
      SystemPropertyTestFilterEnabledExtension,
      FocusEnabledExtension,
      BangTestEnabledExtension,
      SeverityLevelEnabledExtension(testConfigResolver),
   )

   return extensions.fold(Enabled.enabled) { acc, ext -> if (acc.isEnabled) ext.isEnabled(this) else acc }
}
