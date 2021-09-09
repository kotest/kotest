package com.sksamuel.kotest.parallelism

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.Configuration

object ProjectConfig : AbstractProjectConfig() {

   private var start = 0L

   override suspend fun beforeProject() {
      start = System.currentTimeMillis()
   }

   // set the number of threads
   override val parallelism = 10

   // allow all tests to be dispatched at once
   override val concurrentTests: Int = Configuration.MaxConcurrency

   // allow each test its own thread
   override var dispatcherAffinity: Boolean? = false

   override suspend fun afterProject() {
      val duration = System.currentTimeMillis() - start
      // there are 10 tests in the spec, and each one has a delay of 100ms
      // if parallel is working they should all block at the same time
      if (duration > 750)
         error("Parallel execution failure: Execution time was $duration")
   }
}
