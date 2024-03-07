package io.kotest.mpp

import io.kotest.common.TimeMarkCompat
import java.io.FileWriter

val file: FileWriter by lazy { FileWriter(syspropOrEnv("KOTEST_DEBUG_PATH") ?: "kotest.log", false) }

actual fun writeLog(start: TimeMarkCompat, t: Throwable?, f: () -> String) {
   file.write(start.elapsedNow().inWholeMicroseconds.toString())
   file.write("  ")
   file.write(f())
   file.write("\n")
   file.flush()
}
