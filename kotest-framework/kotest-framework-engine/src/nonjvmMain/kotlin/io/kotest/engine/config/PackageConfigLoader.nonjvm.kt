package io.kotest.engine.config

import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.spec.Spec

internal actual fun loadPackageConfigs(spec: Spec): List<AbstractPackageConfig> = emptyList()

internal actual fun loadSystemPropertyConfiguration(): SystemPropertyConfiguration? = null
