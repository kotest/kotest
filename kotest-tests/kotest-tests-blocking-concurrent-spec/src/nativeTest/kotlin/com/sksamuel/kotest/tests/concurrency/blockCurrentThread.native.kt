package com.sksamuel.kotest.tests.concurrency

import kotlin.time.Duration
import platform.posix.usleep

internal actual fun blockCurrentThread(duration: Duration) {
   // usleep()'s behavior is undefined for values >= 1 second on strict POSIX systems, so block in
   // safely-sized chunks rather than a single call.
   var remainingMicros = duration.inWholeMicroseconds
   val chunkMicros = 500_000L
   while (remainingMicros > 0) {
      val step = minOf(remainingMicros, chunkMicros)
      usleep(step.toUInt())
      remainingMicros -= step
   }
}
