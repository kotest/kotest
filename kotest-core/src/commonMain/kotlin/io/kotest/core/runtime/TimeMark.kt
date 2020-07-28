package io.kotest.core.runtime

import com.soywiz.klock.PerformanceCounter

class Mark {
   private val start = PerformanceCounter.milliseconds
   fun elapsed(): Long = (PerformanceCounter.milliseconds - start).toLong()
}
