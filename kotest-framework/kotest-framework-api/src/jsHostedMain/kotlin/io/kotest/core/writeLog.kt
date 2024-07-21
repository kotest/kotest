package io.kotest.core

import io.kotest.common.KotestInternal
import kotlin.time.TimeMark

@KotestInternal
actual fun writeLog(start: TimeMark, t: Throwable?, f: () -> String) {
   console.log(start.elapsedNow().inWholeMicroseconds.toString())
   console.log("  ")
   console.log(f())
   console.log("\n")
}
