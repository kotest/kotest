package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import kotlin.time.Duration.Companion.milliseconds

class ProjectConfig : AbstractProjectConfig() {
   override val invocationTimeout = 2.milliseconds
}
