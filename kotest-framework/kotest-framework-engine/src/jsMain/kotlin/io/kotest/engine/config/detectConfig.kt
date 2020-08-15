package io.kotest.engine.config

/**
 * On the Javascript platform this returns an empty project config.
 *
 * Instead, users set set config directly on the [Configuration] singleton.
 */
actual fun detectConfig(): DetectedProjectConfig = DetectedProjectConfig()
