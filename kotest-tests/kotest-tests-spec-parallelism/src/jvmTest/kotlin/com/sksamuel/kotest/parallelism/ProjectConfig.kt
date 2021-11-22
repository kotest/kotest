package com.sksamuel.kotest.parallelism

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration

object ProjectConfig : AbstractProjectConfig() {

   private var start = 0L

   override suspend fun beforeProject() {
      start = System.currentTimeMillis()
   }

   // set the number of threads so that each test runs in its own thread
   override val parallelism = 10

   override val concurrentSpecs: Int = ProjectConfiguration.MaxConcurrency

   override suspend fun afterProject() {
      val duration = System.currentTimeMillis() - start
      // there are 8 specs, and each one has a delay
      // if parallel is working they should all block at the same time
      if (duration > 700)
         error("Parallel execution failure: Execution time was $duration")
   }
}
