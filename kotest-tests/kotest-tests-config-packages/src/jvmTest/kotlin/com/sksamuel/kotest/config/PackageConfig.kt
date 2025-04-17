package com.sksamuel.kotest.config

import io.kotest.core.config.AbstractPackageConfig
import kotlin.time.Duration.Companion.milliseconds

class PackageConfig : AbstractPackageConfig() {
   override val invocationTimeout = 2.milliseconds
}
