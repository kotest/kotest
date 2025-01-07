package io.kotest.engine.config

import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.extensions.Extension
import io.kotest.engine.extensions.SystemPropertyTagExtension

actual fun loadPackageConfig(packageName: String): AbstractPackageConfig? = null

actual fun loadSystemPropertyConfiguration(): SystemPropertyConfiguration = NoopSystemPropertyConfiguration

actual fun loadPlatformDefaultExtensions(): List<Extension> = listOf(SystemPropertyTagExtension)
