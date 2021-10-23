package io.kotest.mpp

import kotlin.time.TimeMark
import kotlin.time.TimeSource

val start = TimeSource.Monotonic.markNow()

@PublishedApi
internal fun isLoggingEnabled() =
   sysprop("KOTEST_DEBUG")?.uppercase() == "TRUE" || env("KOTEST_DEBUG")?.uppercase() == "TRUE"

fun log(f: () -> String) {
   log(null, f)
}

inline fun log(t: Throwable?, f: () -> String) {
   if (isLoggingEnabled()) {
      println(start.elapsedNow().inWholeMicroseconds.toString() + " " + f())
      if (t != null) println(t)
   }
}

expect fun writeLog(start: TimeMark, t: Throwable?, f: () -> String)
