package io.kotest.core

import io.kotest.common.syspropOrEnv
import java.io.FileWriter
import java.lang.management.ManagementFactory
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
   return if (currentMajorJavaVersion >= 9) {
      getPidFromProcessHandle()
   } else {
      getPidFromMXBean()
   }
}

private fun getPidFromProcessHandle(): Long {
   // FIXME remove ProcessHandle reflection when min supported Java version >= 9
   //return ProcessHandle.current().pid()
   return try {
      val processHandleClass = Class.forName("java.lang.ProcessHandle")
      val currentMethod = processHandleClass.getMethod("current")
      val pidMethod = processHandleClass.getMethod("pid")
      val processHandleInstance = currentMethod.invoke(null)
      pidMethod.invoke(processHandleInstance) as Long
   } catch (_: Exception) {
      getPidFromMXBean()
   }
}

// FIXME remove getPidFromMXBean when min supported Java version >= 9
private fun getPidFromMXBean(): Long {
   val processName = ManagementFactory.getRuntimeMXBean().name
   return processName.substringBefore("@").toLongOrNull() ?: 0
}

private val currentMajorJavaVersion: Int by lazy {
   val version = System.getProperty("java.version")
   if (version.startsWith("1.")) {
      version.substringAfter("1.").substringBefore(".").toInt()
   } else {
      version.substringBefore(".").toInt()
   }
}
