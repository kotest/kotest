package io.kotest.mpp

import io.kotest.common.TimeMarkCompat
import java.io.FileWriter
import kotlin.time.ExperimentalTime

val file: FileWriter by lazy { FileWriter("/home/sam/development/workspace/kotest/kotest/kotest.log", false) }

@ExperimentalTime
actual fun writeLog(start: TimeMarkCompat, t: Throwable?, f: () -> String) {
   file.write(start.elapsedNow().inWholeMicroseconds.toString())
   file.write("  ")
   file.write(f())
   file.write("\n")
   file.flush()
}
