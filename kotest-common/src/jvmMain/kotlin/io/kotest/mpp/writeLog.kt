package io.kotest.mpp

import java.io.FileWriter
import kotlin.time.TimeMark

/**
 * Kotest debug log file ("kotest-PID.log" by default, with PID representing the runner's process ID).
 *
 * The process ID is included because Gradle's `maxParallelForks` > 1 will run multiple processes for Kotest in
 * parallel.
 * `KOTEST_DEBUG_PATH` can be used to specify the log file's path, with the string `PID` being replaced
 * by the respective process ID.
 */
private val file: FileWriter by lazy {
   FileWriter(
      (syspropOrEnv("KOTEST_DEBUG_PATH") ?: "kotest-PID.log").replace("PID", "${ProcessHandle.current().pid()}"),
      false
   )
}

actual fun writeLog(start: TimeMark, t: Throwable?, f: () -> String) {
   file.write(start.elapsedNow().inWholeMicroseconds.toString())
   file.write("  ")
   file.write(f())
   file.write("\n")
   file.flush()
}
