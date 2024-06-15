package io.kotest.mpp

import java.io.FileWriter
import kotlin.time.TimeMark

val file: FileWriter by lazy { FileWriter(syspropOrEnv("KOTEST_DEBUG_PATH") ?: "kotest.log", false) }

actual fun writeLog(start: TimeMark, t: Throwable?, f: () -> String) {
   file.write(start.elapsedNow().inWholeMicroseconds.toString())
   file.write("  ")
   file.write(f())
   file.write("\n")
   file.flush()
}
