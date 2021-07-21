package io.kotest.engine.config

/**
 * Loads a config object from the underlying platform.
 * For example, on the JVM it may scan the classpath and/or look for system properties.
 */
actual fun detectConfig(): DetectedProjectConfig = DetectedProjectConfig()
