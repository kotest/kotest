package io.kotest.core

import io.kotest.common.KotestInternal
import io.kotest.common.reflection.bestName
import io.kotest.common.syspropOrEnv
import kotlin.jvm.JvmName
import kotlin.reflect.KClass
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@KotestInternal
val start by lazy { TimeSource.Monotonic.markNow() }

// Cached once per process: KOTEST_DEBUG is not expected to change mid-run, and this check
// otherwise re-read a sysprop/env var on every single log call across the whole engine.
@PublishedApi
internal val isLoggingEnabled: Boolean by lazy { syspropOrEnv("KOTEST_DEBUG")?.uppercase() == "TRUE" }

@KotestInternal
/**
 * Models a logline for kotest debug.
 * The context is optional and is used to provide the currently executing spec or test.
 */
data class LogLine(val context: String?, val message: String) {
   constructor(context: KClass<*>, message: String) : this(context.bestName(), message)
}

@KotestInternal
class Logger(private val kclass: KClass<*>) {

   companion object {
      inline operator fun <reified T> invoke() = Logger(T::class)
   }

   // inline so f() is never invoked, and no lambda is allocated, when logging is disabled
   @OverloadResolutionByLambdaReturnType
   @JvmName("logpair")
   inline fun log(f: () -> Pair<String?, String>) {
      if (isLoggingEnabled) {
         val (context, message) = f()
         outputLog(LogLine(context, message))
      }
   }

   // inline so f() is never invoked, and no lambda is allocated, when logging is disabled
   @OverloadResolutionByLambdaReturnType
   inline fun log(f: () -> LogLine) {
      if (isLoggingEnabled) {
         outputLog(f())
      }
   }

   @OverloadResolutionByLambdaReturnType
   @JvmName("logsimple")
   // inline so f() is never invoked, and no lambda is allocated, when logging is disabled
   inline fun log(f: () -> String) {
      if (isLoggingEnabled) {
         outputLog(LogLine(null, f()))
      }
   }

   @PublishedApi
   internal fun outputLog(line: LogLine) {
      val message = listOf(
         (kclass.simpleName ?: "").padEnd(60, ' '),
         (line.context ?: "").padEnd(70, ' ').take(70),
         line.message
      ).joinToString("  ")
      writeLog(start, null) { message } // writes to file where possible e.g., jvm
      println(start.elapsedNow().inWholeMilliseconds.toString().padStart(6, ' ') + "  " + message)
   }
}

@KotestInternal
expect fun writeLog(start: TimeMark, t: Throwable?, f: () -> String)
