package io.kotest.mpp

import io.kotest.common.MonotonicTimeSourceCompat
import io.kotest.common.TimeMarkCompat
import kotlin.reflect.KClass
import kotlin.time.ExperimentalTime

val start by lazy { MonotonicTimeSourceCompat.markNow() }

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
expect fun writeLog(start: TimeMarkCompat, t: Throwable?, f: () -> String)
