package io.kotest.engine.test.status

import io.kotest.core.extensions.EnabledExtension
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.engine.extensions.resolvedExtensions

/**
 * Returns [Enabled.isEnabled] if the given [TestCase] is enabled based on default rules at [isEnabledInternal]
 * or any registered [EnabledExtension]s.
 */
suspend fun TestCase.isEnabled(): Enabled {
   val internal = isEnabledInternal()
   return if (!internal.isEnabled) {
      internal
   } else {
      this.spec.resolvedExtensions()
         .filterIsInstance<EnabledExtension>().map { it.isEnabled(descriptor) }.let { Enabled.fold(it) }
   }
}

/**
 * Determines enabled status by using [TestEnabledExtension]s.
 */
internal fun TestCase.isEnabledInternal(): Enabled {

   val extensions = listOf(
      TestConfigEnabledExtension,
      TagsEnabledExtension,
      TestFilterEnabledExtension,
      FocusEnabledExtension,
      BangTestEnabledExtension,
      SeverityLevelEnabledExtension,
   )

   return extensions.fold(Enabled.enabled) { acc, ext -> if (acc.isEnabled) ext.isEnabled(this) else acc }
}
