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
class Logger(private val kclass: KClass<*>) {

   @OverloadResolutionByLambdaReturnType
   fun log(f: () -> Pair<String?, String>) {
      log(null) {
         val (testName, message) = f()
         listOf(
            (kclass.simpleName ?: "").padEnd(60, ' '),
            (testName ?: "").padEnd(70, ' ').take(70),
            message
         ).joinToString("  ")
      }
   }

   @OverloadResolutionByLambdaReturnType
   @JvmName("logsimple")
   fun log(f: () -> String): Unit = log { Pair(null, f()) }
}

@KotestInternal
fun log(f: () -> String) {
   log(null, f)
}

@KotestInternal
fun log(t: Throwable?, f: () -> String) {
   if (isLoggingEnabled()) {
      writeLog(start, t, f)
      println(start.elapsedNow().inWholeMilliseconds.toString() + "  " + f())
      if (t != null) println(t)
   }
}

@KotestInternal
expect fun writeLog(start: TimeMark, t: Throwable?, f: () -> String)
