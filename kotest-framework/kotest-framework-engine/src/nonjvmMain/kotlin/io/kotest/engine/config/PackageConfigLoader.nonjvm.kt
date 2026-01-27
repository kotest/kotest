package io.kotest.engine.config

import io.kotest.common.JVMOnly
import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.spec.Spec

@JVMOnly
internal actual fun loadPackageConfigs(spec: Spec): List<AbstractPackageConfig> = emptyList()

@JVMOnly
internal actual fun loadSystemPropertyConfiguration(): SystemPropertyConfiguration? = null
