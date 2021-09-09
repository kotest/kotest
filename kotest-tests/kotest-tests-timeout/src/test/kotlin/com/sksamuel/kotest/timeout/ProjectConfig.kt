package com.sksamuel.kotest.timeout

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecExecutionOrder
import kotlin.time.Duration

class ProjectConfig : AbstractProjectConfig() {
   override val specExecutionOrder = SpecExecutionOrder.Annotated
   override val timeout = Duration.milliseconds(1000)
}
