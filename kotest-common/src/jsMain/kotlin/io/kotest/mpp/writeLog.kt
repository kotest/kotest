package io.kotest.mpp

import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark

@ExperimentalTime
actual fun writeLog(start: TimeMark, t: Throwable?, f: () -> String) {
   console.log(start.elapsedNow().inWholeMicroseconds.toString())
   console.log("  ")
   console.log(f())
   console.log("\n")
}
