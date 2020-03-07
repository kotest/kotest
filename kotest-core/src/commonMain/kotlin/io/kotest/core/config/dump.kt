package io.kotest.core.config

import io.kotest.mpp.bestName
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Project.createConfigSummary(): String {

   val sb = StringBuilder()

   sb.buildOutput("Parallelism", parallelism().toString() + " thread(s)")
   sb.buildOutput("Default test timeout", timeout().toLongMilliseconds().toString() + "ms")
   sb.buildOutput("Default test order", testCaseOrder()::class.simpleName)
   sb.buildOutput("Default isolation mode", isolationMode()::class.simpleName)
   sb.buildOutput("Global soft assertations", globalAssertSoftly().toString().capitalize())
   sb.buildOutput("Write spec failure file", writeSpecFailureFile().toString().capitalize())
   if (writeSpecFailureFile()) {
      sb.buildOutput("Spec failure file path", specFailureFilePath().capitalize())
   }
   sb.buildOutput("Fail on ignored tests", failOnIgnoredTests().toString().capitalize())
   sb.buildOutput("Spec execution order", specExecutionOrder()::class.simpleName)

   if (extensions().isNotEmpty()) {
      sb.buildOutput("Extensions")
      extensions().map(::mapClassName).forEach {
         sb.buildOutput(it, indentation = 1)
      }
   }

   if (listeners().isNotEmpty()) {
      sb.buildOutput("Listeners")
      listeners().map(::mapClassName).forEach {
         sb.buildOutput(it, indentation = 1)
      }
   }

   if (tags().included.isNotEmpty()) {
      sb.buildOutput("Included Tags")
      tags().included.map(::mapClassName).forEach {
         sb.buildOutput(it, indentation = 1)
      }
   }

   if (tags().included.isNotEmpty()) {
      sb.buildOutput("Excluded Tags")
      tags().excluded.map(::mapClassName).forEach {
         sb.buildOutput(it, indentation = 1)
      }
   }

   return sb.toString()
}

fun Project.dumpProjectConfig() {
   println("~~~ Kotest Configuration ~~~")
   println(createConfigSummary())
}

private fun StringBuilder.buildOutput(key: String, value: String? = null, indentation: Int = 0) {
   if (indentation == 0) {
      append("-> ")
   } else {
      for (i in 0 until indentation) {
         append("  ")
      }
      append("- ")
   }
   append(key)
   value?.let { append(": $it") }
   append("\n")
}

private fun mapClassName(any: Any) = any::class.bestName()
