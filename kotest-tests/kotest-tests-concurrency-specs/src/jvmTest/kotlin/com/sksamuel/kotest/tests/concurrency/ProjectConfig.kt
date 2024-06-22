package com.sksamuel.kotest.tests.concurrency

import io.kotest.common.ExperimentalKotest
import io.kotest.common.testTimeSource
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration
import io.kotest.matchers.comparables.shouldBeLessThan
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark

class ProjectConfig : AbstractProjectConfig() {

   private lateinit var start: TimeMark

   @ExperimentalKotest
   override val concurrentSpecs: Int = ProjectConfiguration.MaxConcurrency

   override suspend fun beforeProject() {
      start = testTimeSource().markNow()
   }

   override suspend fun afterProject() {
      val duration = start.elapsedNow()
      // each of the specs has a 500 milli delay, so the overall time without concurrency would be at least 1500
      // with concurrency it should be ~500
      duration shouldBeLessThan 3.seconds
   }
}
