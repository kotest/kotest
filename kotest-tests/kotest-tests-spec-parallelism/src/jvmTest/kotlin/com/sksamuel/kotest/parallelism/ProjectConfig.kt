package com.sksamuel.kotest.parallelism

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlinx.coroutines.delay

object ProjectConfig : AbstractProjectConfig() {

   private lateinit var start: TimeMark

   override suspend fun beforeProject() {
      // Delay, so that tests can warm up (this is a wild guess - is it actually correct???)
      delay(1.seconds)
      start = TimeSource.Monotonic.markNow()
   }

   // set the number of threads so that each test runs in its own thread
   override val parallelism = 10

   override val concurrentSpecs: Int = ProjectConfiguration.MaxConcurrency

   override suspend fun afterProject() {
      val duration = start.elapsedNow()
      // There are 8 specs, and each one has a 1-second delay.
      // If parallel is working, they should all block at the same time.
      if (duration > 7.seconds) {
         error("Parallel execution failure: Execution time was $duration")
      }
   }
}
