package com.sksamuel.kotest.tests.concurrency

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration
import io.kotest.matchers.longs.shouldBeLessThan

class ProjectConfig : AbstractProjectConfig() {

   private var start = 0L

   @ExperimentalKotest
   override val concurrentSpecs: Int = ProjectConfiguration.MaxConcurrency

   override suspend fun beforeProject() {
      start = System.currentTimeMillis()
   }

   override suspend fun afterProject() {
      val duration = System.currentTimeMillis() - start
      // each of the specs has a 500 milli delay, so the overall time without concurrency would be at least 1500
      // with concurrency it should be ~500
      duration.shouldBeLessThan(1999)
   }
}
