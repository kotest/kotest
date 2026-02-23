package com.sksamuel.kotest

import io.kotest.core.config.AbstractProjectConfig

class ProjectConfig : AbstractProjectConfig() {
   override val retries = 2
}
