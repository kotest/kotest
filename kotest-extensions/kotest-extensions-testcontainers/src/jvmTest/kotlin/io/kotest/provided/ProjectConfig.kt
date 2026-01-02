package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecExecutionOrder

class ProjectConfig : AbstractProjectConfig() {
   override val specExecutionOrder: SpecExecutionOrder = SpecExecutionOrder.Annotated
}
