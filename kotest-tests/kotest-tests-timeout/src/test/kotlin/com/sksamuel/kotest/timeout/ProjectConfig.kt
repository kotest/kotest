package com.sksamuel.kotest.timeout

import io.kotest.core.config.AbstractProjectConfig
import kotlin.time.milliseconds

object ProjectConfig : AbstractProjectConfig() {
   override val timeout = 1000.milliseconds
}
