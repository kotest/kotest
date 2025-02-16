package com.sksamuel.kotest.config

import io.kotest.core.config.AbstractProjectConfig
import kotlin.time.Duration.Companion.milliseconds

class MyProjectConfig : AbstractProjectConfig() {
   override val invocationTimeout = 2.milliseconds
}
