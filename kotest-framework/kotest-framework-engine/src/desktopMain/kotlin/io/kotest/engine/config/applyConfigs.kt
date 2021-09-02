package io.kotest.engine.config

import io.kotest.core.config.Configuration

actual fun applyConfigFromSystemProperties(configuration: Configuration) {}

actual fun applyConfigFromAutoScan(configuration: Configuration) {}

actual fun applyPlatformDefaults(configuration: Configuration) {}
