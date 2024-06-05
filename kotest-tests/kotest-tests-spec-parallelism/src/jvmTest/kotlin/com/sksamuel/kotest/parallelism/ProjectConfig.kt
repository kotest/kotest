package com.sksamuel.kotest.parallelism

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

object ProjectConfig : AbstractProjectConfig() {

   private lateinit var start: TimeMark

   override suspend fun beforeProject() {
      start = TimeSource.Monotonic.markNow()
   }

   // set the number of threads so that each test runs in its own thread
   override val parallelism = 10

   override val concurrentSpecs: Int = ProjectConfiguration.MaxConcurrency

   override suspend fun afterProject() {
      val duration = start.elapsedNow()
      // There are 8 specs, and each one has a 100ms delay.
      // If parallel is working, they should all block at the same time.
      if (duration > 700.milliseconds) {
         error("Parallel execution failure: Execution time was $duration")
      }
   }
}
