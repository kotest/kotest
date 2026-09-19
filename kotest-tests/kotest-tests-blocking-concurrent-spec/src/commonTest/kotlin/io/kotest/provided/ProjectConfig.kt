package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.engine.concurrency.SpecExecutionMode

class ProjectConfig : AbstractProjectConfig() {
   override val specExecutionMode: SpecExecutionMode = SpecExecutionMode.Concurrent
}
