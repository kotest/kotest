package io.kotest.provided

import com.sksamuel.kotest.parallelism.Leases
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.engine.concurrency.TestExecutionMode
import kotlin.time.TimeMark
import kotlin.time.TimeSource

val linux = System.getProperty("os.name").lowercase().contains("linux")

object ProjectConfig : AbstractProjectConfig() {

   private lateinit var start: TimeMark

   override suspend fun beforeProject() {
      start = TimeSource.Monotonic.markNow() // We cannot use virtual time with concurrency
   }

   // allow all tests to be dispatched at once
   override val testExecutionMode: TestExecutionMode = TestExecutionMode.Concurrent

   // allow each test its own thread
   override var dispatcherAffinity: Boolean? = false

   override suspend fun afterProject() {
      val duration = start.elapsedNow()
      // There are 10 tests in the spec, and each one has a delay of 1s.
      // If parallel execution is working, all tests should block at the same time.
      //
      // We allow a large margin of error here as the GitHub runners seem to have a lot of contention.
      if (linux)
         if (Leases.maxLeasesUsed < 10) {
            val cores = Runtime.getRuntime().availableProcessors()
            error(
               "Parallel execution failure: max leases used was ${Leases.maxLeasesUsed} but should have been 10." +
                  " Duration was $duration. Cores: $cores"
            )
         }
   }
}
