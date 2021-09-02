package io.kotest.engine.config

import io.kotest.core.config.Configuration
import io.kotest.engine.extensions.RuntimeTagExtension
import io.kotest.engine.extensions.RuntimeTagExpressionExtension
import io.kotest.engine.extensions.SystemPropertyTagExtension

actual fun applyPlatformDefaults(configuration: Configuration) {
   configuration.registerExtensions(
      SystemPropertyTagExtension,
      RuntimeTagExtension,
      RuntimeTagExpressionExtension
   )
}
