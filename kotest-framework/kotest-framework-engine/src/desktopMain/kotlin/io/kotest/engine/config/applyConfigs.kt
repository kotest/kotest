package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.Configuration

internal actual fun applyConfigFromSystemProperties(configuration: Configuration) {}

internal actual fun applyConfigFromAutoScan(configuration: Configuration) {}

internal actual fun applyPlatformDefaults(configuration: Configuration) {}

internal actual fun detectAbstractProjectConfigs(): List<AbstractProjectConfig> = emptyList()
