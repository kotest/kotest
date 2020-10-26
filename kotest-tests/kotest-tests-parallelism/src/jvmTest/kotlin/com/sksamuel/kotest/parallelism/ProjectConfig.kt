package com.sksamuel.kotest.parallelism

import io.kotest.core.config.AbstractProjectConfig

object ProjectConfig : AbstractProjectConfig() {

   private var start = 0L

   override fun beforeAll() {
      start = System.currentTimeMillis()
   }

   override val parallelism = 10

   override fun afterAll() {
      val duration = System.currentTimeMillis() - start
      // if we ran in parallel the tests should take approx 2 seconds, if there is a bug
      // with parallel then they'd take around 10 seconds
      if (duration > 5000)
         error("Parallel execution failure")
   }
}
