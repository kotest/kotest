package io.kotest.engine.config

import io.kotest.core.extensions.Extension
import io.kotest.engine.extensions.SystemPropertyTagExtension

actual fun loadPlatformDefaultExtensions(): List<Extension> = listOf(SystemPropertyTagExtension)
