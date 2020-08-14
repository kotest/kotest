package io.kotest.engine.config

/**
 * Loads a config object from a platform.
 * For example, on the JVM it may scan the classpath.
 */
expect fun detectConfig(): ProjectConf
