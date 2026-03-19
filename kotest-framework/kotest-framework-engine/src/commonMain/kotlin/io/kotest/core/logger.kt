package io.kotest.core

import io.kotest.common.KotestInternal
import io.kotest.common.syspropOrEnv
import kotlin.jvm.JvmName
import kotlin.reflect.KClass
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@KotestInternal
val start by lazy { TimeSource.Monotonic.markNow() }

@PublishedApi
internal fun isLoggingEnabled() =
   syspropOrEnv("KOTEST_DEBUG")?.uppercase() == "TRUE"

@KotestInternal
data class LogLine(val context: String?, val message: String)

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
      log {
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
      if (isLoggingEnabled()) {
         writeLog(start, null, f) // writes to file where possible eg jvm
         println(start.elapsedNow().inWholeMilliseconds.toString() + "  " + f())
      }
   }
}

@KotestInternal
expect fun writeLog(start: TimeMark, t: Throwable?, f: () -> String)
