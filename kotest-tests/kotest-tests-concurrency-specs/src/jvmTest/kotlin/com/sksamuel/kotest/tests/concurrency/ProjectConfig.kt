package com.sksamuel.kotest.tests.concurrency

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.Configuration
import io.kotest.matchers.longs.shouldBeLessThan

@ExperimentalKotest
object ProjectConfig : AbstractProjectConfig() {

   private var start = 0L

   override val concurrentSpecs: Int = Configuration.MaxConcurrency

   override suspend fun beforeProject() {
      start = System.currentTimeMillis()
   }

   override suspend fun afterProject() {
      val duration = System.currentTimeMillis() - start
      // each of the specs has a 500 milli delay, so the overall time without concurrency would be 1500
      // with concurrency it should be ~500
     // todo duration.shouldBeLessThan(1499)
   }
}
