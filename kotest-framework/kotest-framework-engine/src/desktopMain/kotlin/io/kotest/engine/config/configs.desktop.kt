package io.kotest.engine.config

import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension

internal actual fun loadPlatformDefaultExtensions(): List<Extension> = emptyList()

internal actual fun loadProjectConfigFromReflection(): AbstractProjectConfig? = null

internal actual fun loadPackageConfig(packageName: String): AbstractPackageConfig? = null

internal actual fun loadSystemPropertyConfiguration(): SystemPropertyConfiguration = NoopSystemPropertyConfiguration
