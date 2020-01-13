package io.kotest.core.config

/**
 * Loads a config object from the underlying target.
 * For example, on the JVM it may scan the classpath.
 */
actual fun detectConfig(): ProjectConf = ProjectConf()
