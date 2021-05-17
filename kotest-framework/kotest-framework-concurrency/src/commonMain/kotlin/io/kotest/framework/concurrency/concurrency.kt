package io.kotest.framework.concurrency

import io.kotest.common.ExperimentalKotest

@ExperimentalKotest
data class PatienceConfig constructor(
   val duration: Long = defaultDuration,
   val interval: Interval = defaultInterval
) {
   companion object {
      const val defaultDuration: Long = 3_600_000L
      val defaultInterval = 25L.fixed()
      val default = PatienceConfig(defaultDuration, defaultInterval)
   }
}
