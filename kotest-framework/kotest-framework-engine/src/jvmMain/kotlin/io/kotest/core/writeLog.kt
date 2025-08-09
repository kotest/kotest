package io.kotest.core

import io.kotest.common.syspropOrEnv
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
   val filename = syspropOrEnv("KOTEST_DEBUG_PATH") ?: "kotest-PID.log"
   val pid = getPid()
   FileWriter(
      filename.replace("PID", pid.toString()),
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

private fun getPid(): Long {
   return ProcessHandle.current().pid()
}
