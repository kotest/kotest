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

@PublishedApi
internal fun isLoggingEnabled(): Boolean = syspropOrEnv("KOTEST_DEBUG")?.uppercase() == "TRUE"

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

   @OverloadResolutionByLambdaReturnType
   @JvmName("logpair")
   fun log(f: () -> Pair<String?, String>) {
      log {
         val (context, message) = f()
         LogLine(context, message)
      }
   }

   @OverloadResolutionByLambdaReturnType
   fun log(f: () -> LogLine) {
      outputLog {
         val (context, message) = f()
         listOf(
            (kclass.simpleName ?: "").padEnd(60, ' '),
            (context ?: "").padEnd(70, ' ').take(70),
            message
         ).joinToString("  ")
      }
   }

   @OverloadResolutionByLambdaReturnType
   @JvmName("logsimple")
   fun log(f: () -> String) {
      log { LogLine(null, f()) }
   }

   private fun outputLog(f: () -> String) {
      if (isLoggingEnabled()) {
         writeLog(start, null, f) // writes to file where possible e.g., jvm
         println(start.elapsedNow().inWholeMilliseconds.toString().padStart(6, ' ') + "  " + f())
      }
   }
}

@KotestInternal
expect fun writeLog(start: TimeMark, t: Throwable?, f: () -> String)
