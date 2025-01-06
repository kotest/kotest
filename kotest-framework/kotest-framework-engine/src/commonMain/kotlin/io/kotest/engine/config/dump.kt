package io.kotest.engine.config

import io.kotest.core.config.ProjectConfiguration
import io.kotest.engine.tags.runtimeTagExpression
import io.kotest.mpp.bestName

internal fun ProjectConfiguration.createConfigSummary(): String {

   val sb = StringBuilder()

   sb.buildOutput("Spec execution mode", specExecutionMode::class.simpleName)
   sb.buildOutput("Test execution mode", testExecutionMode::class.simpleName)

   sb.buildOutput("Coroutine debug probe", coroutineDebugProbes.toString())

   sb.buildOutput("Spec execution order", specExecutionOrder.name)
   sb.buildOutput("Default test execution order", testCaseOrder.name)

   sb.buildOutput("Default test timeout", timeout.toString() + "ms")
   sb.buildOutput("Default test invocation timeout", invocationTimeout.toString() + "ms")
   if (projectTimeout != null)
      sb.buildOutput("Overall project timeout", projectTimeout.toString() + "ms")
   sb.buildOutput("Default isolation mode", isolationMode.name)
   sb.buildOutput("Global soft assertions", globalAssertSoftly.toString())
   sb.buildOutput("Write spec failure file", writeSpecFailureFile.toString())
   if (writeSpecFailureFile) {
      sb.buildOutput("Spec failure file path",
         specFailureFilePath.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
   }
   sb.buildOutput("Fail on ignored tests", failOnIgnoredTests.toString())
   sb.buildOutput("Fail on empty test suite", failOnEmptyTestSuite.toString())

   sb.buildOutput("Duplicate test name mode", duplicateTestNameMode.name)

   if (includeTestScopeAffixes != null)
      sb.buildOutput("Include test scope affixes", includeTestScopeAffixes.toString())

   sb.buildOutput("Remove test name whitespace", removeTestNameWhitespace.toString())
   sb.buildOutput("Append tags to test names", testNameAppendTags.toString())

   if (registry.isNotEmpty()) {
      sb.buildOutput("Extensions")
      registry.all().map(::mapClassName).forEach {
         sb.buildOutput(it, indentation = 1)
      }
   }

   runtimeTagExpression().expression.let { sb.buildOutput("Tags", it) }
   return sb.toString()
}

fun ProjectConfiguration.dumpProjectConfig() {
   println("~~~ Kotest Configuration ~~~")
   println(createConfigSummary())
}

private fun StringBuilder.buildOutput(key: String, value: String? = null, indentation: Int = 0) {
   if (indentation == 0) {
      append("-> ")
   } else {
      (0 until indentation).forEach { i ->
        append("  ")
      }
      append("- ")
   }
   append(key)
   value?.let { append(": $it") }
   append("\n")
}

private fun mapClassName(any: Any) = any::class.bestName()
