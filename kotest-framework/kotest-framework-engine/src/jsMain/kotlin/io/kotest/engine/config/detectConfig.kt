package io.kotest.engine.config

/**
 * On the Javascript platform this returns default platform config.
 * Instead, config should be set using the setter methods on [Project].
 */
actual fun detectConfig(): ProjectConf = ProjectConf()
