package io.kotest.engine.config

import io.kotest.core.config.ProjectConfiguration
import io.kotest.engine.extensions.SystemPropertyTagExtension

internal actual fun applyPlatformDefaults(configuration: ProjectConfiguration) {
   configuration.registry.add(SystemPropertyTagExtension)
}
