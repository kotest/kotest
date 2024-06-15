package io.kotest.mpp

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalForeignApi::class)
actual fun timeInMillis(): Long {
   return memScoped {
      val timespec = alloc<platform.posix.timespec>().ptr
      platform.posix.clock_gettime(platform.posix.CLOCK_REALTIME.convert(), timespec)
      @OptIn(UnsafeNumber::class)
      timespec.pointed
         .run { tv_sec.seconds + tv_nsec.nanoseconds }
         .inWholeMilliseconds
   }
}
