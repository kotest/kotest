package io.kotest.mpp

import kotlin.time.TimeMark
import kotlin.time.TimeSource

val start = TimeSource.Monotonic.markNow()

@PublishedApi
internal fun isLoggingEnabled() = true
//  sysprop("KOTEST_DEBUG")?.uppercase() == "TRUE" || env("KOTEST_DEBUG")?.uppercase() == "TRUE"

fun log(f: () -> String) {
   log(null, f)
}

fun log(t: Throwable?, f: () -> String) {
   if (isLoggingEnabled()) {
      writeLog(start, t, f)
      println(start.elapsedNow().inWholeMicroseconds.toString() + " " + f())
      if (t != null) println(t)
   }
}

expect fun writeLog(start: TimeMark, t: Throwable?, f: () -> String)
