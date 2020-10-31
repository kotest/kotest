package com.sksamuel.kotest.concurrency.test

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ConcurrencyMode
import io.kotest.core.spec.SpecExecutionOrder

class ProjectConfig : AbstractProjectConfig() {
   override val concurrencyMode = ConcurrencyMode.Test
   override val specExecutionOrder = SpecExecutionOrder.Annotated
}
