package io.kotest.mpp

import kotlin.reflect.KClass
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import io.kotest.common.MonotonicTimeSourceCompat

val start by lazy { MonotonicTimeSourceCompat.markNow() } // TODO #3052
val startMillis by lazy { timeInMillis() } // TODO #3052

@PublishedApi
internal fun isLoggingEnabled() =
   syspropOrEnv("KOTEST_DEBUG")?.uppercase() == "TRUE"

class Logger(private val kclass: KClass<*>) {
   fun log(f: () -> Pair<String?, String>) {
      log(null) {
         val (testName, message) = f()
         listOf(
            (kclass.simpleName ?: "").padEnd(50, ' '),
            (testName ?: "").padEnd(70, ' ').take(70),
            message
         ).joinToString("  ")
      }
   }
}

@OverloadResolutionByLambdaReturnType
fun log(f: () -> String) {
   log(null, f)
}

fun log(t: Throwable?, f: () -> String) {
   if (isLoggingEnabled()) {
      writeLog(start, t, f)
      println(start.elapsedNow().inWholeMicroseconds.toString() + "  " + f())
      if (t != null) println(t)
   }
}

@ExperimentalTime
expect fun writeLog(start: TimeMark, t: Throwable?, f: () -> String)
