package io.kotest.engine.config

import io.kotest.core.config.Configuration
import io.kotest.engine.extensions.SystemPropertyTagExtension

internal actual fun applyPlatformDefaults(configuration: Configuration) {
   configuration.registry.add(SystemPropertyTagExtension)
}
