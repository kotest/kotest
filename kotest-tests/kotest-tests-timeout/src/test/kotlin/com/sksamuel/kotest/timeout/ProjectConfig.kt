package com.sksamuel.kotest.timeout

import io.kotest.core.config.AbstractProjectConfig
import kotlin.time.Duration
import kotlin.time.milliseconds

object ProjectConfig : AbstractProjectConfig() {
   override val timeout = Duration.milliseconds(1000)
}
