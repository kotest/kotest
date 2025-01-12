package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.engine.concurrency.SpecExecutionMode
import kotlin.time.TimeSource

val start = TimeSource.Monotonic.markNow()

class ProjectConfig : AbstractProjectConfig() {
   override val specExecutionMode: SpecExecutionMode = SpecExecutionMode.Concurrent
}
