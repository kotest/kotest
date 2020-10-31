package com.sksamuel.kotest.concurrency.test

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ConcurrencyMode

class ProjectConfig : AbstractProjectConfig() {
   override val concurrencyMode = ConcurrencyMode.Test
}
