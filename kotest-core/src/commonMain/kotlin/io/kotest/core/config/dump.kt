package io.kotest.core.config

import io.kotest.core.bestName

fun Project.dumpProjectConfig() {

   println("~~~ Project Configuration ~~~")
   buildOutput("Parallelism", parallelism().toString() + " thread(s)")
   buildOutput("Test order", specExecutionOrder()::class.simpleName)
   buildOutput("Soft assertations", globalAssertSoftly().toString().capitalize())
   buildOutput("Write spec failure file", writeSpecFailureFile().toString().capitalize())
   buildOutput("Fail on ignored tests", failOnIgnoredTests().toString().capitalize())

   if (extensions().isNotEmpty()) {
      buildOutput("Extensions")
      extensions().map(::mapClassName).forEach {
         buildOutput(it, indentation = 1)
      }
   }

   if (testListeners().isNotEmpty()) {
      buildOutput("Listeners")
      testListeners().map(::mapClassName).forEach {
         buildOutput(it, indentation = 1)
      }
   }

   if (filters().isNotEmpty()) {
      buildOutput("Filters")
      filters().map(::mapClassName).forEach {
         buildOutput(it, indentation = 1)
      }
   }
}

private fun buildOutput(key: String, value: String? = null, indentation: Int = 0) {
   StringBuilder().apply {
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
   }.also { println(it.toString()) }
}

private fun mapClassName(any: Any) = any::class.bestName()
