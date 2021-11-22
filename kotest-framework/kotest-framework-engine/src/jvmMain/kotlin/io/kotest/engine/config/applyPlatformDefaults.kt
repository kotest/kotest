package io.kotest.engine.config

import io.kotest.engine.extensions.SystemPropertyTagExtension

internal actual fun applyPlatformDefaults(configuration: MutableConfiguration) {
   configuration.registry().add(SystemPropertyTagExtension)
}
