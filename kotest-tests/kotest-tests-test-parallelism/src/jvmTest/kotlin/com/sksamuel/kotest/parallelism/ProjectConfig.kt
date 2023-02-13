package com.sksamuel.kotest.parallelism

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration

object ProjectConfig : AbstractProjectConfig() {

   private var start = 0L

   override suspend fun beforeProject() {
      start = System.currentTimeMillis()
   }

   // set the number of threads
   override val parallelism = 10

   // allow all tests to be dispatched at once
   override val concurrentTests: Int = ProjectConfiguration.MaxConcurrency

   // allow each test its own thread
   override var dispatcherAffinity: Boolean? = false

   override suspend fun afterProject() {
      val duration = System.currentTimeMillis() - start
      // there are 10 tests in the spec, and each one has a delay of 1s
      // if parallel is working they should all block at the same time
      //
      // We allow a large margin of error here as the GitHub runner seems to have a lot of contention
      if (Leases.maxLeasesUsed < parallelism) {
         val cores = Runtime.getRuntime().availableProcessors()
         error("Parallel execution failure: max leases used was ${Leases.maxLeasesUsed} but should have been 10. Duration was $duration ms. Cores: $cores")
      }
   }
}
