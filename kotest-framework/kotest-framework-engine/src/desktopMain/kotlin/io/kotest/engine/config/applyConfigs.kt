package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig

internal actual fun applyConfigFromSystemProperties(configuration: MutableConfiguration) {}

internal actual fun applyConfigFromAutoScan(configuration: MutableConfiguration) {}

internal actual fun applyPlatformDefaults(configuration: MutableConfiguration) {}

internal actual fun detectAbstractProjectConfigs(): List<AbstractProjectConfig> = emptyList()
