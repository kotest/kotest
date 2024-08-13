package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig

class ProjectConfig : AbstractProjectConfig() {
   override val invocationTimeout: Long = 2
}
