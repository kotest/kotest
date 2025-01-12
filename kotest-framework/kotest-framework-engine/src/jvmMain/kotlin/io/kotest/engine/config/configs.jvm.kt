package io.kotest.engine.config

import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import io.kotest.engine.extensions.SystemPropertyTagExtension

internal actual fun loadPlatformDefaultExtensions(): List<Extension> = listOf(SystemPropertyTagExtension)

internal actual fun loadPackageConfigs(spec: Spec): List<AbstractPackageConfig> = PackageConfigLoader.configs(spec)

internal actual fun loadProjectConfigFromReflection(): AbstractProjectConfig? = ProjectConfigLoader.detect()
